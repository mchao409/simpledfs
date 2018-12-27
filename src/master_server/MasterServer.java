package master_server;
import java.io.*;
import java.util.*;

import network.FileContents;
import network.MessagePackage;
import network.TCPConnection;
import server.TCPServer;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MasterServer extends TCPServer {
	private HashMap<String, String> files;
	private HashSet<String> file_names;
	private final String DB_PATH = "src/server_db/";

	public MasterServer(int port) throws IOException {
		super(port);
		files = new HashMap<String, String>();
		file_names = new HashSet<String>();
	}
	
	protected void handleInput(TCPConnection s, MessagePackage msg) throws IOException {
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
		FileContents file = msg.getFileContents();
		String file_name = new String(file.getName());
		if(file_names.contains(file_name)) {
			// TODO handle
		}
		Files.write(Paths.get(DB_PATH + file_name), file.getContents());
		file_names.add(file_name);
		// TODO
	}

	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, MessagePackage msg) throws IOException {
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
		// TODO second byte: get minion id
		String file_name = msg.getMessage();
		String path = DB_PATH + file_name;
		File f = new File(path);
		byte[] contents = null;
		try {
			contents = Files.readAllBytes(f.toPath());
			if(!f.delete()) throw new NoSuchFileException(path);
			file_names.remove(file_name);
		} catch (NoSuchFileException e) {

		} 
		FileContents file = new FileContents(file_name.getBytes(), contents);
		s.send(new MessagePackage(1, null, file));
		// TODO notifyAll

	}
	
//	private void insert(BufferedReader input) throws IOException {
//		String file_name = input.readLine();
//		// TODO
//	}

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.start();
    }
}