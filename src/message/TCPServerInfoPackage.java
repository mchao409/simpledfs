package message;

import java.io.Serializable;

import network.TCPConnection;
import network.TCPServerInfo;

public class TCPServerInfoPackage extends MessagePackage {
	private TCPServerInfo server_info;
	private String message;
	
	public TCPServerInfoPackage(String command, TCPServerInfo server_info) {
		super(command);
		this.server_info = server_info;
	}
	
	public TCPServerInfoPackage(String command, String message, TCPServerInfo server_info) {
		this(command, server_info);
		this.message = message;
	}
	
	public TCPServerInfo getServerInfo() {
		return server_info;
	}
	
	public String getMessage() {
		return message;
	}
}
