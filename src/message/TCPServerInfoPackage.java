package message;

import java.io.Serializable;
import java.util.List;

import network.TCPConnection;
import network.TCPServerInfo;

public class TCPServerInfoPackage extends MessagePackage {
	List<TCPServerInfo> servers;
	private TCPServerInfo server_info;
	private String message;
	
	public TCPServerInfoPackage(String command, List<TCPServerInfo> servers) {
		super(command);
		this.servers = servers;
	}
	
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
	
	public List<TCPServerInfo> getServers() {
		return servers;
	}
}
