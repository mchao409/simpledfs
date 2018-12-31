package slave_server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashSet;

import message.FileContentsPackage;
import message.MessagePackage;
import message.QueryPackage;
import message.SlaveInfoPackage;
import server.Constants;
import server.TCPServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;

public class SlaveServer extends TCPServer {
	private String master_ip;
	private int master_port;
	private TCPConnection master;
	private HashSet<String> file_paths;
	private final String DB_PATH = "src/server_db/";
	
	public SlaveServer(int port, String master_ip, int master_port) {
		super(port);
		this.master_ip = master_ip;
		this.master_port = master_port;
		file_paths = new HashSet<String>();
		try {
			master = new TCPConnection(new Socket(master_ip, master_port));
		} catch(IOException e) {
			System.out.println("No connection to master server, try again.");
			return;
		}
		get_server_data();
		listen_to_master();
	}
	
	/**
	 * Retrieves all of the files from another slave server
	 */
	private void get_server_data() {
		// Send master an initial query to get all the server data
		master.send(new QueryPackage(3, new SlaveInfoPackage(3,"127.0.0.1", port))); // TODO fix ip
	}
	
	/**
	 * Continuously listens to master for incoming changes
	 */
	private void listen_to_master() {
		// continuously listen to master
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = Constants.COMMANDS[msg.getCommand()];
		switch(command) {
		case "add": // Add new file
			add_file(s, (FileContentsPackage)msg);
			break;
		case "read": // Read file
			read_file(s, (FileContentsPackage) msg);
			break;
		case "delete": // Delete file
			delete_file(s, (FileContentsPackage)msg);
			break;
		case "client": 
			break;
		default: 
			break;
		}
	}

	/**
	 * Adds a new file to the server
	 */
	private void add_file(TCPConnection s, FileContentsPackage msg) throws IOException {
			master.send(msg);
			FileContentsPackage resp = (FileContentsPackage) master.read();
			FileContents file = resp.getFileContents();
			String file_name = new String(file.getName());
			Files.write(Paths.get(DB_PATH + file_name), file.getContents());
			synchronized(file_paths) {
				file_paths.add(file_name);
			}
			if(resp.getMessage().equals(Constants.ADD_SUCCESS)) {
				s.send(new FileContentsPackage(0, "File successfully added", null));
			}
			else {
				s.send(new FileContentsPackage(0, "File could not be added", null));
			}
			// TODO send response to s
	}

	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, FileContentsPackage msg) throws IOException {
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
		s.send(new FileContentsPackage(1, null, file));
	}

	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private void delete_file(TCPConnection s, FileContentsPackage msg) throws IOException {
		master.send(msg);
		FileContentsPackage resp = (FileContentsPackage) master.read();
		String message = resp.getMessage();
		if(message.equals(Constants.FILE_DOES_NOT_EXIST)) {
			s.send(new FileContentsPackage(2, "The file you chose does not exist", null));
		}
		else if (message.equals(Constants.DELETE_SUCCESS)) {
			FileContents file = resp.getFileContents();
			String file_name = new String(file.getName());
			String path = DB_PATH + file_name;
			File f = new File(path);
			byte[] contents = Files.readAllBytes(f.toPath());
			f.delete();
			synchronized(file_paths) {
				file_paths.remove(file_name);
			}
			s.send(new FileContentsPackage(2, null, new FileContents(file_name.getBytes(), contents)));
		}
		else s.send(null); // TODO handle
	}
	
	public static void main(String[] args) throws IOException {
		SlaveServer m = new SlaveServer(7999, "127.0.0.1", 9095);
		m.listen();
	}
	
}
