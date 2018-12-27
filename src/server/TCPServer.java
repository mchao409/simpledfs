package server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import network.MessagePackage;
import network.TCPConnection;

public abstract class TCPServer {
	protected int port;
	
	public TCPServer(int port) {
		this.port = port;
	}
	
	public void start() throws IOException {
		listen();
	}
	
	/**
	 * Listens for incoming connections to the server
	 * @throws IOException
	 */
	protected void listen() throws IOException {
		ServerSocket listener = new ServerSocket(port);
        try {
            while(true) {
            	Socket socket;
            	try {
                    socket = listener.accept(); // TODO handle potential IOException
            	} catch(IOException e) {
            		continue;
            	}
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
                    			System.out.println("Disconnect");
                    			break;
                    		}
                    	}
                    } catch(SocketException e) {
                    	System.out.println("Disconnect");
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
	
	protected abstract void handleInput(TCPConnection s, MessagePackage msg) throws IOException;
}
