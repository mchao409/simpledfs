package server;
import java.io.*;
import java.util.*;

import simpledfs.Constants;

import java.net.*;

public class MasterServer {
	private int startingPort;
//	private ArrayList<String> files;
	private HashMap<String, String> file_contents;
	private HashSet<String> file_names;

	public MasterServer(int startingPort) throws IOException {
		this.startingPort = startingPort;
//		files = new ArrayList<String>();
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
	
	private void listen() throws IOException{
		ServerSocket client_listener = new ServerSocket(startingPort);
        try {
            while(true) {
                Socket socket = client_listener.accept();
                Thread t = new Thread(() -> {
                    try {
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                    	while(true) {
                    		if(input.available() == 0) {
                    			continue;
                    		}
                    		handleInput(input);
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
	
	private void handleInput(DataInputStream input) throws IOException {
		int val = input.read();
		String command = Constants.CLIENT_COMMANDS[val];
		switch(command) {
		case "add": // Add new file
			add_file(input);
			break;
		case "read": // Read file
			break;
		case "delete": // Delete file
			break;
		default: 
			break;
		}
	}
	
	private void add_file(DataInputStream input) throws IOException {
		String contents = getStringFromStream(input);
		int newLine = contents.indexOf("\n");
		String file_name = contents.substring(0, newLine);
		String file_contents = contents.substring(newLine + 1);
		// TODO
	}
	
	private void read(DataInputStream input) throws IOException {
		
		
	}
	private void delete_file(DataInputStream input) throws IOException {
		String file_name = getStringFromStream(input).trim();
		// TODO

	}
	
//	private void insert(BufferedReader input) throws IOException {
//		String file_name = input.readLine();
//		// TODO
//	}
	
	private String getStringFromStream(DataInputStream input) throws IOException {
		int length = input.read();
		byte[] arr = new byte[length];
		input.read(arr,0,length);
		return new String(arr);
	}
	
	

    public static void main(String[] args) throws IOException {
    	MasterServer master = new MasterServer(9095);
    	master.start();
    }
}