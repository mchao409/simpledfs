package slave_server;

import java.io.IOException;

import server.TCPServer;
import network.MessagePackage;
import network.TCPConnection;

public class SlaveServer extends TCPServer {
	private String master_ip;
	private int master_port;
	
	
	public SlaveServer(int port, String master_ip, int master_port) throws IOException {
		super(port);
		this.master_ip = master_ip;
		this.master_port = master_port;
	}
	
	public void start() throws IOException {
	}

	@Override
	protected void handleInput(TCPConnection s, MessagePackage msg) throws IOException {
		// TODO Auto-generated method stub
		
	}	
	
}
