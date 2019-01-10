package server.master_server;
import java.io.*;
import java.util.*;

import message.ChunkLocationPackage;
import message.FileChunkInfoPackage;
import message.FileChunkPackage;
import message.FileContentsPackage;
import message.FileInfoPackage;
import message.MessagePackage;
import message.QueryPackage;
import message.TCPServerInfoPackage;
import network.FileContents;
import network.TCPConnection;
import network.TCPServerInfo;
import server.Constants;
import server.TCPServer;
import server.master_server.log.FileLog;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MasterServer extends TCPServer {

	/**
	 * Maps from TCPServerInfo to a slave server to an Integer representing the number of clients the slave 
	 * server is handling currently
	 */
	private HashMap<TCPServerInfo, Integer> num_connects;
	
	/**
	 * Maps from TCPServerInfo to an Integer representing the number of chunks saved on the slave server currently
	 */
	private HashMap<TCPServerInfo, Integer> num_chunks_saved;
	
	/**
	 * Number of slave servers that should hold each chunk
	 */
	private static int CHUNK_DISTR_CONST = 3;

	private HashMap<String, FileLog> file_storage_data;
	
	
	public MasterServer(int port) throws IOException {
		super(port);
		num_connects = new HashMap<TCPServerInfo, Integer>();
		num_chunks_saved = new HashMap<TCPServerInfo, Integer>();
		file_storage_data = new HashMap<String,FileLog>();
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = msg.getCommand();
		switch(command) {
		
		case Constants.IS_FILE_ADDED: // client checks if a file has been completely added 
			is_file_added(s, (FileInfoPackage)msg);
			break;
		
		case Constants.IS_FILE_DELETED:
			is_file_deleted(s, (FileInfoPackage) msg);
			break;
		
		case Constants.CHUNK_ADDED:
			log_added_chunk((FileChunkInfoPackage) msg); // slave server notifies that it has added a chunk
			break;
		
		case Constants.CHUNK_DELETED:
			log_deleted_chunk((FileChunkInfoPackage) msg);
			break;
			
		case Constants.READ_FILE: // Query from client to read a file
			read(s, (FileInfoPackage) msg);
			break;
			
		case Constants.DELETE_FILE: // notification from client to delete a file
			delete_file(s,(FileInfoPackage)msg);
			break;
			
		case Constants.NEW_SLAVE: // new minion connection
			new_slave(s, (TCPServerInfoPackage) msg);
			break;
			
		case Constants.CLIENT: // initial client query
			client_initial_query(s, msg);
			break;
			
		case Constants.HANDLING_CLIENT: // notification that slave is handling a client
			TCPServerInfoPackage slave = (TCPServerInfoPackage) msg;
			String message = slave.getMessage();
			if(message.equals(Constants.CURRENTLY_HANDLING_CLIENT)) {
				synchronized(num_connects) {
					num_connects.put(slave.getServerInfo(), num_connects.get(slave.getServerInfo()) + 1);
				}
			}
			else if (message.equals(Constants.DONE_HANDLING_CLIENT)){
				synchronized(num_connects) {
					num_connects.put(slave.getServerInfo(), num_connects.get(slave.getServerInfo())-1);
				}
			}
			break;
		default: 
			break;
		}
	}

	/**
	 * Checks if a file has been completely added (ie each chunk has been added at least once)
	 * 	 and sends an appropriate response to the client
	 * @param client
	 * @param pkg
	 */
	private synchronized void is_file_added(TCPConnection client, FileInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) == null) {
			client.send(new QueryPackage(Constants.FILE_DOES_NOT_EXIST));
		}
		else if(file_storage_data.get(identifier).get_num_chunks() != pkg.get_num_chunks()) {
			client.send(new QueryPackage(Constants.FILE_NOT_ADDED));
		}
		else {
			client.send(new QueryPackage(Constants.FILE_ADDED));
		}
	}
	
	private synchronized void is_file_deleted(TCPConnection client, FileInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) == null) {
			client.send(new QueryPackage(Constants.FILE_DELETED));
		}
		else {
			client.send(new QueryPackage(Constants.FILE_NOT_DELETED));
		}
	}
	
	/**
	 * Log an added chunk and its location
	 */
	private synchronized void log_added_chunk(FileChunkInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) == null) {
			FileLog f = new FileLog(identifier);
			file_storage_data.put(identifier, f);
		}
		file_storage_data.get(identifier).add_chunk_location(pkg.get_start(), pkg.get_slave());
		TCPServerInfo slave = pkg.get_slave();
		if(num_chunks_saved.get(slave) == null) {
			num_chunks_saved.put(slave, 1);
		}
		else {
			num_chunks_saved.put(slave, num_chunks_saved.get(slave)+1);
		}
	}
	
	/**
	 * Log a deleted chunk and its location
	 */
	private synchronized void log_deleted_chunk(FileChunkInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) != null) {
			FileLog f = file_storage_data.get(identifier);
			f.remove_slave_location(pkg.get_start(), pkg.get_slave());
			if(f.get_num_chunks() == 0) {
				file_storage_data.remove(identifier);
				
			}
		}
		TCPServerInfo slave = pkg.get_slave();
		if(num_chunks_saved.get(slave) != null) {
			num_chunks_saved.put(slave, Math.max(0, num_chunks_saved.get(slave)-1));
		}
	}
	
	/**
	 * Send information about locations of all chunks of a file to client
	 */
	private synchronized void read(TCPConnection client, FileInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		FileLog f = file_storage_data.get(identifier);
		if(f == null) {
			client.send(new ChunkLocationPackage(Constants.READ_FILE, null, identifier));
		}
		else client.send(new ChunkLocationPackage(Constants.READ_FILE, f.chunk_locs, identifier));
	}
	
	/**
	 * Handle a delete request to the file system, notify necessary slave servers to delete
	 * @throws IOException
	 */
	private synchronized void delete_file(TCPConnection client, FileInfoPackage msg) throws IOException {
		String identifier = msg.get_identifier();
		FileLog log = file_storage_data.get(identifier);
		if(log == null) {
			System.out.println("The file named \"" + identifier + "\" does not exist on the master server");
			return;
		}
		
		HashMap<Integer, List<TCPServerInfo>> chunk_locs = log.chunk_locs;
		for(Integer start: chunk_locs.keySet()) {
			List<TCPServerInfo> slaves = chunk_locs.get(start);
			for(TCPServerInfo slave : slaves) {
				TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
				connect.send(new FileChunkInfoPackage(Constants.DELETE_CHUNK, identifier, start));
			}
		}
	}
	
	/**
	 * Handle the starting up of a new slave server, redirects it to another slave to retrieve db info
	 */
	private void new_slave(TCPConnection slave, TCPServerInfoPackage msg) {
		assert msg.getCommand().equals(Constants.NEW_SLAVE);
		synchronized(num_connects) {
			num_connects.put(msg.getServerInfo(), 0);
		}
		synchronized(num_chunks_saved) {
			num_chunks_saved.put(msg.getServerInfo(), 0);

		}
//		slave.send(new TCPServerInfoPackage(null, get_least_occupied_slave(msg.getServerInfo())));
		
	}
	
//	private TCPServerInfo get_least_occupied_slave(TCPServerInfo ignore) {
//		TCPServerInfo min_connects = null;
//		int min = Integer.MAX_VALUE;
//		synchronized(num_connects) {
//			for(TCPServerInfo slave : num_connects.keySet()) {
//				int curr_connects = num_connects.get(slave);
//				if(curr_connects < min && !(ignore != null && ignore.equals(slave))) {
//					min = num_connects.get(slave);
//					min_connects = slave;
//				}
//			}
//		}
//		return min_connects;
//	}
	
	private List<TCPServerInfo> get_least_chunks_slaves() {
		ArrayList<TCPServerInfo> slaves = new ArrayList<TCPServerInfo>();
		synchronized(num_chunks_saved) {
			for(int i = 0; i < CHUNK_DISTR_CONST; i++) {
				TCPServerInfo min_connects = null;
				int min = Integer.MAX_VALUE;
				for(TCPServerInfo slave : num_chunks_saved.keySet()) {
					int curr_connects = num_chunks_saved.get(slave);
					if(curr_connects < min && !slaves.contains(slave)) {
						min = num_chunks_saved.get(slave);
						min_connects = slave;
					}
				}
				slaves.add(min_connects);
			}
		}
		return slaves;
	}
	
	/**
	 * Handle a client's initial query, send information about slave server to contact
	 */
	private void client_initial_query(TCPConnection client, MessagePackage msg) {
		List<TCPServerInfo> least_chunks_saved = get_least_chunks_slaves();
		client.send(new TCPServerInfoPackage(null, least_chunks_saved));
	}

}