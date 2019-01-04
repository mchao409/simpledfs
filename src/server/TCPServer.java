package server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import message.FileContentsPackage;
import message.MessagePackage;
import network.TCPConnection;

public abstract class TCPServer {
	protected int port;
	protected ServerSocket listener;
	public TCPServer(int port) {
		this.port = port;
	}
	
	/**
	 * Listens for incoming connections to the server
	 * @throws IOException
	 */
	public void listen() throws IOException {
		listener = new ServerSocket(port);
		System.out.println("Listening on port " + port);
        try {
            while(true) { 
            	// Listen forever for incoming connections
            	Socket socket;
            	try {
                    socket = listener.accept(); // TODO handle potential IOException
            	} catch(IOException e) {
            		continue;
            	}
                Thread t = new Thread(() -> { 
                	// Listen forever to socket connection
                    try {
                    	TCPConnection s = new TCPConnection(socket);
                    	MessagePackage msg;
                    	while(true) { 
                    		try {
                          		 msg = (MessagePackage) s.read();
                         		if(msg == null) {
                         			continue;
                         		}
                         		handle_input(s, msg);
                    		} catch (EOFException | SocketException e) {
                    			// socket disconnected
//                    			e.printStackTrace();
                    			System.out.println("Disconnected");
                    			break;
                    		}
                    	}
                    } catch(SocketException e) {
                    	System.out.println("Disconnected");
//                    	e.printStackTrace();
                    } catch(IOException e) {
                    	e.printStackTrace();
                    }
                });
                t.start();
            }
        } 
        finally {
        	listener.close();
        }
	}
	
	public void close() {
		 try {
			listener.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	}
	
	/**
	 * Handle the message sent to the server
	 * @param s
	 * @param msg
	 * @throws IOException
	 */
	protected abstract void handle_input(TCPConnection s, MessagePackage msg) throws IOException;
}
