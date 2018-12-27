package server;
import java.io.*;
import java.util.*;

import network.MessagePackage;
import network.SocketConnection;
import simpledfs.Constants;
import simpledfs.FileContents;

import java.net.*;

public class MasterServer {
	private int startingPort;
	private HashMap<String, String> files;

	public MasterServer(int startingPort) throws IOException {
		this.startingPort = startingPort;
		files = new HashMap<String, String>();
	}
	
	public void start() {
		Thread t = new Thread(() -> {
			try {
				listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
	}
	
	/**
	 * Listens for incoming connections to the server
	 * @throws IOException
	 */
	private void listen() throws IOException{
		ServerSocket client_listener = new ServerSocket(startingPort);
        try {
            while(true) {
                Socket socket = client_listener.accept();
                Thread t = new Thread(() -> {
                    try {
                    	SocketConnection s = new SocketConnection(socket);
                    	MessagePackage msg;
                    	while(true) {
                    		 msg = (MessagePackage) s.read();
                    		if(msg == null) {
                    			continue;
                    		}
                    		handleInput(s, msg);
                    	}
                    } catch(IOException e) {
                    	e.printStackTrace();
                    } catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
                });
                t.start();
            }
        }
        finally {
            client_listener.close();
        }
	}
	
	private void handleInput(SocketConnection s, MessagePackage msg) throws IOException, ClassNotFoundException {
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
	 * @param input
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private void add_file(SocketConnection s, MessagePackage msg) {
		String contents;
			FileContents file = msg.getFileContents();
			String file_name = new String(file.getName());
			String file_contents = new String(file.getContents());
			files.put(file_name, file_contents);
	
		// TODO
	}

	private void read_file(SocketConnection s, MessagePackage msg) throws IOException, ClassNotFoundException {
		String file_name = msg.getMessage();
		String contents = files.get(file_name);
		if(contents == null) {
			contents = "ERROR: File does not exist";
		}
		byte[] contents_bytes = contents.getBytes();
		FileContents file = new FileContents(file_name.getBytes(), contents.getBytes());
		s.send(new MessagePackage(1, null, file));
	}
	
	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private void delete_file(SocketConnection s, MessagePackage msg) throws IOException {
		// TODO second byte: get minion id
		String file_name = msg.getMessage();
		String contents = files.remove(file_name);
		if(contents == null) {
			contents = "ERROR: File does not exist";
		}
		FileContents file = new FileContents(file_name.getBytes(), contents.getBytes());
		s.send(new MessagePackage(1, null, file));

		// TODO notifyAll

	}
	
//	private void insert(BufferedReader input) throws IOException {
//		String file_name = input.readLine();
//		// TODO
//	}
	
//	private String getStringFromStream(SocketConnection s) throws IOException {
//		int length = s.read();
//		byte[] arr = new byte[length];
//		s.read(arr,0,length);
//		return new String(arr);
//	}
	
	

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.start();
    }
}