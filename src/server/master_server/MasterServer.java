package server.master_server;
import java.io.*;
import java.util.*;

import message.ChunkLocationPackage;
import message.FileChunkInfoPackage;
import message.FileChunkPackage;
import message.FileContentsPackage;
import message.FileNamePackage;
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
	 * Maps from TCPConnection to a slave server to an Integer representing the number of clients the slave 
	 * server is handling currently
	 */
	private HashMap<TCPServerInfo, Integer> num_connects;
	
	/**
	 * Number of slave servers that should hold each chunk
	 */
	private static int CHUNK_DISTR_CONST = 3;
	

	private HashMap<String, FileLog> file_storage_data;
	
	
	public MasterServer(int port) throws IOException {
		super(port);
		num_connects = new HashMap<TCPServerInfo, Integer>();
		file_storage_data = new HashMap<String,FileLog>();
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = msg.getCommand();
		switch(command) {
		
		case Constants.CHUNK_ADDED:
			log_added_chunk((FileChunkInfoPackage) msg); // slave server notifies that it has added a chunk
			break;
		
		case Constants.CHUNK_DELETED:
			log_deleted_chunk((FileChunkInfoPackage) msg);
			break;
			
		case Constants.READ: // Query from client to read a file
			read(s, (FileNamePackage) msg);
			break;
			
		case Constants.DELETE: // notification from client to delete a file
			delete_file(s,(FileNamePackage)msg);
			break;
			
		case Constants.NEW_SLAVE: // new minion connection
			new_slave(s, (TCPServerInfoPackage) msg);
			break;
			
		case Constants.CLIENT: // initial client query
			client_initial_query(s, msg);
			break;
			
//		case Constants.PRINT_ALL:
//			System.out.println(file_names);
//			break;
			
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
	 * Log an added chunk and its location
	 */
	private synchronized void log_added_chunk(FileChunkInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) == null) {
			FileLog f = new FileLog(identifier);
			file_storage_data.put(identifier, f);
		}
		file_storage_data.get(identifier).add_chunk_location(pkg.get_start(), pkg.get_slave());
	}
	
	/**
	 * Log a deleted chunk and its location
	 */
	private synchronized void log_deleted_chunk(FileChunkInfoPackage pkg) {
		String identifier = pkg.get_identifier();
		if(file_storage_data.get(identifier) != null) {
			FileLog f = file_storage_data.get(identifier);
			f.remove_slave_location(pkg.get_start(), pkg.get_slave());
		}
	}
	
	/**
	 * Send information about locations of all chunks of a file to client
	 */
	private synchronized void read(TCPConnection client, FileNamePackage pkg) {
		String identifier = pkg.get_identifier();
		FileLog f = file_storage_data.get(identifier);
		if(f == null) {
			client.send(new ChunkLocationPackage(Constants.READ, null, identifier));
		}
		else client.send(new ChunkLocationPackage(Constants.READ, f.chunk_locs, identifier));
	}
	
	/**
	 * Handle a delete request to the file system, notify necessary slave servers to delete
	 * @throws IOException
	 */
	private synchronized void delete_file(TCPConnection client, FileNamePackage msg) throws IOException {
		String identifier = msg.get_identifier();
		FileLog log = file_storage_data.get(identifier);
		if(log == null) {
			System.out.println("File does not exist");
			return;
		}
		
		HashMap<Integer, List<TCPServerInfo>> chunk_locs = log.chunk_locs;
		for(Integer start: chunk_locs.keySet()) {
			List<TCPServerInfo> slaves = chunk_locs.get(start);
			for(TCPServerInfo slave : slaves) {
				TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
				connect.send(new FileChunkInfoPackage(Constants.DELETE, identifier, start));
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
		slave.send(new TCPServerInfoPackage(null, get_least_occupied_slave(msg.getServerInfo())));
		
	}
	
	private TCPServerInfo get_least_occupied_slave(TCPServerInfo ignore) {
		TCPServerInfo min_connects = null;
		int min = Integer.MAX_VALUE;
		synchronized(num_connects) {
			for(TCPServerInfo slave : num_connects.keySet()) {
				int curr_connects = num_connects.get(slave);
				if(curr_connects < min && !(ignore != null && ignore.equals(slave))) {
					min = num_connects.get(slave);
					min_connects = slave;
				}
			}
		}
		return min_connects;
	}
	
	private List<TCPServerInfo> get_least_occupied_slaves() {
		ArrayList<TCPServerInfo> slaves = new ArrayList<TCPServerInfo>();

		synchronized(num_connects) {
			for(int i = 0; i < CHUNK_DISTR_CONST; i++) {
				TCPServerInfo min_connects = null;
				int min = Integer.MAX_VALUE;
				for(TCPServerInfo slave : num_connects.keySet()) {
					int curr_connects = num_connects.get(slave);
					if(curr_connects < min && !slaves.contains(slave)) {
						min = num_connects.get(slave);
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
		List<TCPServerInfo> least_occ_slaves = get_least_occupied_slaves();
		client.send(new TCPServerInfoPackage(null, least_occ_slaves));
	}

}