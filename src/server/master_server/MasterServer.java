package server.master_server;
import java.io.*;
import java.util.*;

import message.FileContentsPackage;
import message.MessagePackage;
import message.QueryPackage;
import message.TCPServerInfoPackage;
import network.FileContents;
import network.TCPConnection;
import network.TCPServerInfo;
import server.Constants;
import server.TCPServer;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MasterServer extends TCPServer {
	/**
	 * Set of all file paths stored
	 */
	private HashSet<String> file_names;	

	/**
	 * Maps from TCPConnection to a slave server to an Integer representing the number of clients the slave 
	 * server is handling currently
	 */
	private HashMap<TCPServerInfo, Integer> num_connects;
	

	public MasterServer(int port) throws IOException {
		super(port);
		file_names = new HashSet<String>();
		num_connects = new HashMap<TCPServerInfo, Integer>();
	}
	
	protected void handle_input(TCPConnection s, MessagePackage msg) throws IOException {
		String command = msg.getCommand();
		switch(command) {
		case Constants.ADD: // notification from slave server that a client wishes to add
			add_file(s, (FileContentsPackage)msg);
			break;
			
		case Constants.DELETE: // notification from slave server that a client wishes to delete
			delete_file(s,(FileContentsPackage)msg);
			break;
			
		case Constants.NEW_SLAVE: // new minion connection
			new_slave(s, (TCPServerInfoPackage) msg);
			break;
			
		case Constants.CLIENT: // initial client query
			client_initial_query(s, msg);
			break;
			
		case Constants.PRINT_ALL:
			System.out.println(file_names);
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
		
		case Constants.ADD_CHUNK:
			
			break;
		default: 
			break;
		}
	}
	
	/**
	 * Send a message to all slave servers except for the one indicated in the argument
	 * @param ignore_sender a package containing information about the sender to ignore
	 * @param msg
	 */
	private void notifyAllExceptSender(FileContentsPackage ignore_sender) {
		// Needs to be tested
		TCPServerInfo sender = ignore_sender.getSenderOfPackage();
		synchronized(num_connects) {
			for(TCPServerInfo slave : num_connects.keySet()) {
				Thread t = new Thread(() -> {
					if(!sender.equals(slave)) {
						try  {
							TCPConnection to_send = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
							to_send.send(ignore_sender);
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		}

	}

	/**
	 * Handle an add request to the file system
	 * @param slave the slave server that received the request from the client
	 * @param msg
	 * @throws IOException
	 */
	private void add_file(TCPConnection slave, FileContentsPackage msg) throws IOException {
		assert msg.getCommand().equals(Constants.ADD);
		if(!num_connects.containsKey(slave)) {
			// TODO handle
		}
		FileContents file = msg.getFileContents();
		String file_name = new String(file.getName());
		synchronized(file_names) { 
			if(file_names.contains(file_name)) {
				slave.send(new FileContentsPackage(Constants.ADD,Constants.FILE_ALREADY_EXISTS, null));
			}
			else {
				file_names.add(file_name);
				notifyAllExceptSender(new FileContentsPackage(Constants.ADD_MASTER,null, file, msg.getSenderOfPackage()));
				slave.send(new FileContentsPackage(Constants.ADD, Constants.ADD_SUCCESS, msg.getFileContents()));
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
		assert msg.getCommand().equals(Constants.DELETE);
		if(!num_connects.containsKey(slave)) {
			// TODO handle
		}
		String file_name = msg.getMessage();
		synchronized(file_names) {
			if(file_names.contains(file_name)) {
				file_names.remove(file_name);
				synchronized(slave) {
					slave.send(new FileContentsPackage(Constants.DELETE, Constants.DELETE_SUCCESS, new FileContents(file_name.getBytes(), null)));
				}
				notifyAllExceptSender(new FileContentsPackage(Constants.DELETE_MASTER,null, new FileContents(file_name.getBytes(), null), msg.getSenderOfPackage()));
			}
			else {
				synchronized(slave) {
					slave.send(new FileContentsPackage(Constants.DELETE, Constants.FILE_DOES_NOT_EXIST, null));
				}
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
	
	/**
	 * Handle a client's initial query, send information about slave server to contact
	 */
	private void client_initial_query(TCPConnection client, MessagePackage msg) {
		TCPServerInfoPackage resp = new TCPServerInfoPackage(null, get_least_occupied_slave(null));
		client.send(resp);
	}

}