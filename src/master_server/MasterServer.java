package master_server;
import java.io.*;
import java.util.*;

import message.FileContentsPackage;
import message.MessagePackage;
import message.QueryPackage;
import message.SlaveInfoPackage;
import network.FileContents;
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
	
	private HashMap<TCPConnection, SlaveInfoPackage> slaves;

	/**
	 * Maps from TCPConnection to a slave server to an Integer representing the number of clients the slave 
	 * server is handling currently
	 */
	private HashMap<TCPConnection, Integer> num_connects;
	

	public MasterServer(int port) throws IOException {
		super(port);
		file_names = new HashSet<String>();
		num_connects = new HashMap<TCPConnection, Integer>();
		slaves = new HashMap<TCPConnection, SlaveInfoPackage>();
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = Constants.COMMANDS[msg.getCommand()];
		switch(command) {
		case "add": // notification from slave server
			add_file(s, (FileContentsPackage)msg);
			break;
			
		case "delete": // notification from slave server
			delete_file(s,(FileContentsPackage)msg);
			break;
			
		case "new_slave": // new minion connection
			new_slave(s, (QueryPackage) msg);
			break;
			
		case "client": // initial client query
			client_initial_query(s, msg);
			break;
			
		case "get_all_files":
			break;
			
		default: 
			break;
		}
	}
	
	/**
	 * Send a message to all slave servers except for the one indicated
	 * @param ignore
	 * @param msg
	 */
	private void notifyAllExcept(TCPConnection ignore, MessagePackage msg) {
		// Needs to be tested
		for(TCPConnection slave : num_connects.keySet()) {
			if(slave.hasSameRemote(ignore)) continue;
			slave.send(msg);
		}
	}

	/**
	 * Handle an add request to the file system
	 * @param slave the slave server that received the request from the client
	 * @param msg
	 * @throws IOException
	 */
	private void add_file(TCPConnection slave, FileContentsPackage msg) throws IOException {
		assert msg.getCommand() == 0;
		if(!num_connects.containsKey(slave)) {
			// TODO handle
		}
		FileContents file = msg.getFileContents();
		String file_name = new String(file.getName());
		synchronized(file_names) { 
			if(file_names.contains(file_name)) {
				slave.send(new FileContentsPackage(0,Constants.FILE_ALREADY_EXISTS, null));
			}
			else {
				file_names.add(file_name);
				notifyAllExcept(slave, msg);
				slave.send(new FileContentsPackage(0, Constants.ADD_SUCCESS, msg.getFileContents()));
			}
		}
	}

	/**
	 * Handle a delete request to the file system
	 * @param slave the slave server that received the request from the client
	 * @param msg
	 * @throws IOException
	 */
	private void delete_file(TCPConnection slave, FileContentsPackage msg) throws IOException {
		assert msg.getCommand() == 2;
		if(!num_connects.containsKey(slave)) {
			// TODO handle
		}
		String file_name = msg.getMessage();
		synchronized(file_names) {
			if(file_names.contains(file_name)) {
				file_names.remove(file_name);
				synchronized(slave) {
					slave.send(new FileContentsPackage(2, Constants.DELETE_SUCCESS, new FileContents(file_name.getBytes(), null)));
				}
				notifyAllExcept(slave, msg);
			}
			else {
				synchronized(slave) {
					slave.send(new FileContentsPackage(2, Constants.FILE_DOES_NOT_EXIST, null));
				}
			}
		}
	}
	
	private void new_slave(TCPConnection slave, QueryPackage msg) {
		assert msg.getCommand() == 3;
		synchronized(num_connects) {
			num_connects.put(slave, 0);
		}
		synchronized(slaves) {
			slaves.put(slave, msg.getSlaveInfo());
		}
		// TODO send slave information
		
	}
	
	private void client_initial_query(TCPConnection client, MessagePackage msg) {
		TCPConnection min_connects = null;
		int min = Integer.MAX_VALUE;
		synchronized(num_connects) {
			for(TCPConnection slave : num_connects.keySet()) {
				int curr_connects = num_connects.get(slave);
				if(curr_connects < min) {
					min = num_connects.get(slave);
					min_connects = slave;
				}
			}
		}
		SlaveInfoPackage resp = null;
		synchronized(slaves) {
			resp = slaves.get(min_connects);
		}
		client.send(resp);
	}
	

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.listen();
    }
}