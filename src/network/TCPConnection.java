package network;

import java.net.*;
import java.io.*;

public class TCPConnection {
	private Socket socket;
	private ObjectOutputStream output_stream;
	private ObjectInputStream input_stream;
	
	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		output_stream = new ObjectOutputStream(socket.getOutputStream());
		input_stream = new ObjectInputStream(socket.getInputStream());
	}
	
	public boolean send(MessagePackage m) {
		try {
			output_stream.writeObject(m);
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Object read() throws IOException {
		try {
			return input_stream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int inputAvailable() throws IOException {
		return input_stream.available();
	}
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof TCPConnection)) return false;
		return ((TCPConnection)other).socket == socket;
	}
}
