package server;
import java.io.*;
import java.util.*;

import network.FileContents;
import network.MessagePackage;
import network.TCPConnection;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MasterServer {
	private int startingPort;
	private HashMap<String, String> files;
	private HashSet<String> file_names;

	public MasterServer(int startingPort) throws IOException {
		this.startingPort = startingPort;
		files = new HashMap<String, String>();
		file_names = new HashSet<String>();
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
	private void listen() throws IOException {
		ServerSocket client_listener = new ServerSocket(startingPort);
        try {
            while(true) {
                Socket socket = client_listener.accept();
                Thread t = new Thread(() -> {
                    try {
                    	TCPConnection s = new TCPConnection(socket);
                    	MessagePackage msg;
                    	while(true) {
                    		try {
                          		 msg = (MessagePackage) s.read();
                         		if(msg == null) {
                         			continue;
                         		}
                         		handleInput(s, msg);
                    		} catch (EOFException | SocketException e) {
                    			// socket disconnected
                    			System.out.println("disconnect");
                    			break;
                    		}
                    	}
                    } catch(SocketException e) {
                    	System.out.println("disconnect");
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
	
	private void handleInput(TCPConnection s, MessagePackage msg) throws IOException, ClassNotFoundException {
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
		Files.write(Paths.get("src/server_db/" + file_name), file.getContents());
		file_names.add(file_name);
		// TODO
	}

	/**
	 * Sends the the contents of a file
	 */
	private void read_file(TCPConnection s, MessagePackage msg) throws IOException, ClassNotFoundException {
		String file_name = msg.getMessage();
		String path = "src/server_db/" + file_name;
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
		String path = "src/server_db/" + file_name;
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