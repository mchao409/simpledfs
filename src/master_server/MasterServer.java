package master_server;
import java.io.*;
import java.util.*;

import network.FileContents;
import network.MessagePackage;
import network.TCPConnection;
import server.Constants;
import server.TCPServer;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
// TODO test notifyAll
public class MasterServer extends TCPServer {
	/**
	 * Set of all file paths stored
	 */
	private HashSet<String> file_names;
	
	/**
	 * Maps from TCPConnection to a slave server to an Integer representing the number of clients the slave 
	 * server is handling currently
	 */
	private HashMap<TCPConnection, Integer> numConnects;	

	public MasterServer(int port) throws IOException {
		super(port);
		file_names = new HashSet<String>();
		numConnects = new HashMap<TCPConnection, Integer>();
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = Constants.COMMANDS[msg.getCommand()];
		switch(command) {
		case "add":
			add_file(s, msg);
			// TODO notify other servers
			break;
		case "delete":
			delete_file(s,msg);
			break;
		case "new_minion": // new minion connection
			break;
		case "client": // initial client query
			break;
		case "get_all_files":
			break;
		default: 
			break;
		}
	}
	
	private void notifyAllExcept(TCPConnection ignore, MessagePackage msg) {
		// Needs to be tested
		for(TCPConnection slave : numConnects.keySet()) {
			if(slave.equals(ignore)) continue;
			slave.send(msg);
		}
	}

	/**
	 * Adds a new file to the server
	 */
	private void add_file(TCPConnection slave, MessagePackage msg) throws IOException {
		if(!numConnects.containsKey(slave)) {
			// TODO handle
		}
		FileContents file = msg.getFileContents();
		String file_name = new String(file.getName());
		synchronized(file_names) { // TODO synchronized
			if(file_names.contains(file_name)) {
				slave.send(new MessagePackage(0,Constants.FILE_ALREADY_EXISTS, null));
			}
			else {
				file_names.add(file_name);
				notifyAllExcept(slave, msg);
				slave.send(new MessagePackage(0, Constants.ADD_SUCCESS, msg.getFileContents()));
			}
		}
	}

	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private void delete_file(TCPConnection slave, MessagePackage msg) throws IOException {
		if(!numConnects.containsKey(slave)) {
			// TODO handle
		}
		String file_name = msg.getMessage();
		synchronized(file_names) {
			if(file_names.contains(file_name)) {
				file_names.remove(file_name);
				slave.send(new MessagePackage(2, Constants.DELETE_SUCCESS, new FileContents(file_name.getBytes(), null)));
				notifyAllExcept(slave, msg);
			}
			else {
				slave.send(new MessagePackage(2, Constants.FILE_DOES_NOT_EXIST, null));
			}
		}
	}
	

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.listen();
    }
}