package server;
import java.io.*;
import java.util.*;

import network.SocketConnection;
import simpledfs.Constants;

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
//                        DataInputStream input = new DataInputStream(socket.getInputStream());
                    	while(true) {
                    		if(s.inputAvailable() == 0) {
                    			continue;
                    		}
                    		handleInput(s);
                    	}
                    } catch(IOException e) {
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
	
	private void handleInput(SocketConnection s) throws IOException {
		int val = s.read();
		System.out.println(val + " value");
//		if(val == 4) {
//			System.out.println(s.read());
//		}
//		System.out.println("here");
		
		String command = Constants.COMMANDS[val];
		System.out.println(command);
		switch(command) {
		case "add": // Add new file
			System.out.println("add called");
			add_file(s);
			break;
		case "read": // Read file
			System.out.println("read called");
			read_file(s);
			break;
		case "delete": // Delete file
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
	 */
	private void add_file(SocketConnection s) throws IOException {
		String contents = getStringFromStream(s);
		int newLine = contents.indexOf("\n");
		String file_name = contents.substring(0, newLine).trim();
		String file_contents = contents.substring(newLine + 1).trim();
		files.put(file_name, file_contents);
		
		// TODO
	}

	private void read_file(SocketConnection s) throws IOException {
		String file_name = getStringFromStream(s).trim();
		String contents = files.get(file_name);
		byte[] contents_bytes = contents.getBytes();
		byte[] to_send = new byte[contents_bytes.length+1];
		System.arraycopy(contents_bytes, 0, to_send, 1, contents_bytes.length);
		to_send[0] = (byte)contents_bytes.length;
		s.send(to_send);
	}
	
	/**
	 * Deletes file from the server
	 * @param input
	 * @throws IOException
	 */
	private void delete_file(SocketConnection s) throws IOException {
		// TODO second byte: get minion id
		String file_name = getStringFromStream(s).trim();
		String result = files.remove(file_name);
		if(result == null) {
		}
		else {
			
		}
		// TODO notifyAll

	}
	
//	private void insert(BufferedReader input) throws IOException {
//		String file_name = input.readLine();
//		// TODO
//	}
	
	private String getStringFromStream(SocketConnection s) throws IOException {
		int length = s.read();
		byte[] arr = new byte[length];
		s.read(arr,0,length);
		return new String(arr);
	}
	
	

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.start();
    }
}