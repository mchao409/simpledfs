package network;

import java.io.Serializable;

public class TCPServerInfo implements Serializable {
	private String address;
	private int port;
	
	public TCPServerInfo(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof TCPServerInfo)) return false;
		TCPServerInfo other_server = (TCPServerInfo) other;
		return other_server.address.equals(address) && other_server.port == port;
	}
	
	@Override
	public int hashCode() {
		return address.hashCode() + port;
	}
	
	
	
	@Override
	public String toString() {
		return "" + address + " " + port;
	}
	
}
