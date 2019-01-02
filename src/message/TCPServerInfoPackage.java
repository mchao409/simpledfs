package message;

import java.io.Serializable;

import network.TCPConnection;
import network.TCPServerInfo;

public class TCPServerInfoPackage extends MessagePackage {
	private TCPServerInfo server_info;
	
	public TCPServerInfoPackage(int command, TCPServerInfo server_info) {
		super(command);
		this.server_info = server_info;
	}
	
	public TCPServerInfo getServerInfo() {
		return server_info;
	}
}
