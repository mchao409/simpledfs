package network;

import java.net.*;

import message.MessagePackage;

import java.io.*;

/**
 * Wrapper class for Socket with synchronous operations
 */
public class TCPConnection {
	private Socket socket;
	private ObjectOutputStream output_stream;
	private ObjectInputStream input_stream;
	
	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		output_stream = new ObjectOutputStream(socket.getOutputStream());
		input_stream = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Send a message to the socket
	 */
	public synchronized boolean send(MessagePackage m) {
		try {
			synchronized(output_stream) {
				output_stream.writeObject(m);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Read from the socket
	 */
	public synchronized Object read() {
		try {
			return input_stream.readObject();
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
			return null;
		} catch(IOException e) {
//			e.printStackTrace();
			return null;
		}
	}
}
