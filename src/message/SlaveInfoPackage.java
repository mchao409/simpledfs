package message;

import java.io.Serializable;

import network.TCPConnection;

public class SlaveInfoPackage extends MessagePackage {
	private String address;
	private int port;
	
	public SlaveInfoPackage(int command, String address, int port) {
		super(command);
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
}
