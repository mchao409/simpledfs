package slave_server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashSet;

import server.Constants;
import server.TCPServer;
import network.FileContents;
import network.MessagePackage;
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
			System.out.println("No connection to master server.");
		}
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = Constants.COMMANDS[msg.getCommand()];
		switch(command) {
		case "add": // Add new file
			add_file(s, msg);
			break;
		case "read": // Read file
			read_file(s, msg);
			break;
		case "delete": // Delete file
			delete_file(s, msg);
			break;
		case "new_minion": // new minion connection
			break;
		case "client": // initial client query
			break;
		default: 
			break;
		}
	}

	/**
	 * Adds a new file to the server
	 */
	private void add_file(TCPConnection s, MessagePackage msg) throws IOException {
			master.send(msg);
			MessagePackage resp = (MessagePackage) master.read();
			FileContents file = resp.getFileContents();
			String file_name = new String(file.getName());
			Files.write(Paths.get(DB_PATH + file_name), file.getContents());
			synchronized(file_paths) {
				file_paths.add(file_name);
			}
			if(resp.getMessage().equals(Constants.ADD_SUCCESS)) {
				s.send(new MessagePackage(0, "File successfully added", null));
			}
			else {
				s.send(new MessagePackage(0, "File could not be added", null));
			}

			// TODO send response to s
	}

	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, MessagePackage msg) throws IOException {
		System.out.println("here");
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
		s.send(new MessagePackage(1, null, file));
	}

	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private void delete_file(TCPConnection s, MessagePackage msg) throws IOException {
		master.send(msg);
		MessagePackage resp = (MessagePackage) master.read();
		String message = resp.getMessage();
		if(message.equals(Constants.FILE_DOES_NOT_EXIST)) {
			s.send(new MessagePackage(2, "The file you chose does not exist", null));
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
			s.send(new MessagePackage(2, null, new FileContents(file_name.getBytes(), contents)));
		}
		else s.send(null); // TODO handle
	}
	
	public static void main(String[] args) throws IOException {
		SlaveServer m = new SlaveServer(7999, "127.0.0.1", 9095);
		m.listen();
	}
	
}
