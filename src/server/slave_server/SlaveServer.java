package server.slave_server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashSet;

import file.FileChunk;
import message.FileChunkInfoPackage;
import message.FileChunkPackage;
import message.FileContentsPackage;
import message.MessagePackage;
import message.MultipleFilesPackage;
import message.QueryPackage;
import message.TCPServerInfoPackage;
import server.Constants;
import server.TCPServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import network.TCPServerInfo;

public class SlaveServer extends TCPServer {
	private String master_ip;
	private int master_port;
	private TCPConnection master;
	private String DB_PATH;
	private TCPServerInfo slave_info;
	
	public SlaveServer(int port, String master_ip, int master_port) throws IOException {
		super(port);
		this.master_ip = master_ip;
		this.master_port = master_port;
		slave_info = new TCPServerInfo("127.0.0.1", port);
		
		try {
			master = new TCPConnection(new Socket(master_ip, master_port));
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("No connection to master server, try again.");
			return;
		} 
		DB_PATH = "src/server_db" + port + "/";
		new File(DB_PATH).mkdir();
	}
	
	@Override() 
	public void listen() throws IOException {
		notify_master();
		super.listen();
	}
	
	/**
	 * Notify master of slave server startup
	 */
	private void notify_master() {
		master.send(new TCPServerInfoPackage(Constants.NEW_SLAVE, new TCPServerInfo("127.0.0.1", port)));
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = msg.getCommand();
		switch(command) {
		case Constants.ADD_CHUNK: // notification from client to add a chunk
			add_chunk(s, (FileChunkPackage)msg);
			break;
			
		case Constants.READ_CHUNK: // notification from client to read a chunk
			read_file(s, (FileChunkInfoPackage) msg);
			break;
			
		case Constants.DELETE_CHUNK: // notification from master to delete a chunk
			delete_file(s, (FileChunkInfoPackage)msg);
			break;

		
		case Constants.GET_ALL_FILES: // send data about all files over
//			get_all_files(s);
			break;
			
		default:  // should never be called
			break;
		}
	}
	
	/**
	 * Notify master that the slave server is either begun or finished handling a client
	 * @param starting
	 */
	private void notify_master_client(boolean starting) {
		if(starting) {
			master.send(new TCPServerInfoPackage(Constants.HANDLING_CLIENT, Constants.CURRENTLY_HANDLING_CLIENT, slave_info));
		}
		else {
			master.send(new TCPServerInfoPackage(Constants.HANDLING_CLIENT, Constants.DONE_HANDLING_CLIENT, slave_info));
		}
	}

	/**
	 * Adds a new file to the server
	 */
	private synchronized void add_chunk(TCPConnection s, FileChunkPackage msg) throws IOException {
//		notify_master_client(true);
		if(add_chunk_to_db(msg)) {
			master.send(new FileChunkInfoPackage(Constants.CHUNK_ADDED, msg.get_identifier(),
					msg.get_chunk().get_start(), slave_info));
			// todo notify master chunk was added
		}
	}
	
	private boolean add_chunk_to_db(FileChunkPackage pkg) {
		FileChunk chunk = pkg.get_chunk();
		String save_name = pkg.get_identifier() + chunk.get_start();
		try {
			Files.write(Paths.get(DB_PATH + save_name), chunk.get_byte_arr());
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, FileChunkInfoPackage msg) throws IOException {
		notify_master_client(true);
		String identifier = msg.get_identifier();
		int start = msg.get_start();
		String path = DB_PATH + identifier + start;
		File f = new File(path);
		byte[] contents = null;
		try {
			contents = Files.readAllBytes(f.toPath());
			
		} catch (NoSuchFileException e) {

		} 
		if(contents == null) {
			s.send(null);
		}
		else {
			FileChunk chunk = new FileChunk(start, contents);
			s.send(new FileChunkPackage(Constants.READ_CHUNK, identifier, chunk));
		}
		notify_master_client(false);
	}

	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private synchronized void delete_file(TCPConnection s, FileChunkInfoPackage msg) throws IOException {
		notify_master_client(true);
		String save_name = msg.get_identifier() + msg.get_start();
		File f = new File(DB_PATH + save_name);
		f.delete();
		master.send(new FileChunkInfoPackage(Constants.CHUNK_DELETED, msg.get_identifier(), msg.get_start(), slave_info));
		notify_master_client(false);
	}
	
//	/**
//	 * Send all db data
//	 * @param args
//	 * @throws IOException
//	 */
//	private void get_all_files(TCPConnection slave) {
//		MultipleFilesPackage pkg = new MultipleFilesPackage();
//		for(String file_name : file_paths) {
//			try {
//				String path = DB_PATH + file_name;
//				File f = new File(path);
//				byte[] contents = Files.readAllBytes(f.toPath());
//				pkg.addFile(new FileContents(file_name.getBytes(), contents));
//			} catch(IOException e) {
//				e.printStackTrace();
//			}
//		}
//		slave.send(pkg);
//	}

	
}
