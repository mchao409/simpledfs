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
	private HashSet<String> file_paths;
	private String DB_PATH;
	private TCPServerInfo slave_info;
	
	public SlaveServer(int port, String master_ip, int master_port) throws IOException {
		super(port);
		this.master_ip = master_ip;
		this.master_port = master_port;
		file_paths = new HashSet<String>();
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
	public void listen() {
		Thread t = new Thread(() -> {
			try {
				super.listen();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}); 
		t.start();
		get_server_data();

	}
	
	/**
	 * Retrieves all of the files from another slave server
	 */
	private void get_server_data() {
		// Send master an initial query to get all the server data
		master.send(new TCPServerInfoPackage(Constants.NEW_SLAVE, new TCPServerInfo("127.0.0.1", port))); // TODO fix ip
		TCPServerInfoPackage slave_to_get_from = (TCPServerInfoPackage) master.read();
		TCPServerInfo slave = slave_to_get_from.getServerInfo();
		if(slave == null) {
			return;
		}
		try {
			TCPConnection slave_connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
			slave_connect.send(new TCPServerInfoPackage(Constants.GET_ALL_FILES, slave_info));
			MultipleFilesPackage pkg = (MultipleFilesPackage) slave_connect.read();
			for(FileContents f : pkg) {
				add_file_to_db(f); 
			}
		} catch(IOException e) {
			System.out.println("Could not retrieve server data");
		}
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = msg.getCommand();
		switch(command) {
		case Constants.ADD: // notification from client to add a file
			add_file(s, (FileChunkPackage)msg);
			break;
			
		case Constants.READ: // notification from client to read a file
			read_file(s, (FileContentsPackage) msg);
			break;
			
		case Constants.DELETE: // notification from master to delete a file
			delete_file(s, (FileChunkInfoPackage)msg);
			break;
		
		case Constants.PRINT_ALL:  // used for debugging
			System.out.println(file_paths);
			break;
		
//		case Constants.ADD_MASTER: // notification from master a file has been added to the system
//			FileContents file_to_add = ((FileContentsPackage)msg).getFileContents();
//			add_file_to_db(file_to_add);
//			break;
			
//		case Constants.DELETE_MASTER: // notification from master a file has been deleted from the system
//			FileContents file_to_delete = ((FileContentsPackage)msg).getFileContents();
//			delete_file_from_db(file_to_delete);
//			break;
		
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
	private synchronized void add_file(TCPConnection s, FileChunkPackage msg) throws IOException {
//		notify_master_client(true);
		if(add_chunk_to_db(msg)) {
			master.send(new FileChunkInfoPackage(Constants.CHUNK_ADDED, msg.get_identifier(),
					msg.get_chunk().get_start(), slave_info));
			// todo notify master chunk was added
		}

//		notify_master_client(false);
		
//		notify_master_client(true);
//		FileContentsPackage resp;
//		synchronized(master) {
//			msg.addSender(new TCPServerInfo("127.0.0.1", port));
//			master.send(msg);
//			resp = (FileContentsPackage) master.read();
//		}
//
//		FileContents file = resp.getFileContents();
//		if(resp.getMessage().equals(Constants.ADD_SUCCESS)) {
//			add_file_to_db(file);
//			String file_name = new String(file.getName());
//			s.send(new FileContentsPackage(Constants.ADD, "File successfully added", null));
//		}
//		else {
//			s.send(new FileContentsPackage(Constants.ADD, "File could not be added", null));
//		}
//		notify_master_client(false);
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
	
//	private void read_chunk_from_db(TCPConnection s, FileChunkInfoPackage pkg) {
//		notify_master_client(true);
//		String save_name = pkg.get_identifier() + pkg.get_start() + "_" + pkg.get_end();
//		String path = DB_PATH + save_name;
//		
//	}

	/**
	 * Save file to database
	 */
	private void add_file_to_db(FileContents file) throws IOException {
		String file_name = new String(file.getName());
		Files.write(Paths.get(DB_PATH + file_name), file.getContents());
		synchronized(file_paths) {
			file_paths.add(file_name);
		}
	}
	
	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, FileContentsPackage msg) throws IOException {
		notify_master_client(true);
		String file_name = msg.getMessage();
		String path = DB_PATH + file_name;
		File f = new File(path);
		byte[] contents = null;
		try {
			contents = Files.readAllBytes(f.toPath());
		} catch (NoSuchFileException e) {
			// TODO deal with this
		} 
		FileContents file = new FileContents(file_name.getBytes(), contents);
		s.send(new FileContentsPackage(Constants.READ, null, file));
		notify_master_client(false);
	}

	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private synchronized void delete_file(TCPConnection s, FileChunkInfoPackage msg) throws IOException {
		String save_name = msg.get_identifier() + msg.get_start();
		File f = new File(DB_PATH + save_name);
		f.delete();
		master.send(new FileChunkInfoPackage(Constants.CHUNK_DELETED, msg.get_identifier(), msg.get_start(), slave_info));
		
//		notify_master_client(true);
//		FileContentsPackage resp;
//		synchronized(master) {
//			msg.addSender(new TCPServerInfo("127.0.0.1", port));
//			master.send(msg);
//			resp = (FileContentsPackage) master.read();
//		}
//		String message = resp.getMessage();
//		if(message.equals(Constants.FILE_DOES_NOT_EXIST)) {
//			s.send(new FileContentsPackage(Constants.DELETE, "The file you chose does not exist", null));
//		}
//		else if (message.equals(Constants.DELETE_SUCCESS)) {
//			FileContents file = resp.getFileContents();
//			String file_name = new String(file.getName());
//			byte[] contents = delete_file_from_db(file);
//			s.send(new FileContentsPackage(Constants.DELETE, null, new FileContents(file_name.getBytes(), contents)));
//		}
//		else s.send(null); // TODO handle
//		notify_master_client(false);
	}
	
	
	
//	/**
//	 * Delete file from database
//	 */
//	private byte[] delete_file_from_db(FileContents file) throws IOException {
//		String file_name = new String(file.getName());
//		String path = DB_PATH + file_name;
//		File f = new File(path);
//		byte[] contents = Files.readAllBytes(f.toPath());
//		f.delete();
//		file_paths.remove(file_name);
//		return contents;
//	}
	
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
