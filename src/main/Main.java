package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import master_server.MasterServer;
import server.TCPServer;
import slave_server.SlaveServer;

public class Main {
	public int num_slave;
	public int slave_starting_port;
	public int master_port;
	public String master_ip;
	
	private List<TCPServer> servers;
	public Main(int num_slave, int slave_starting_port, int master_port, String master_ip) {
		this.num_slave = num_slave;
		this.slave_starting_port = slave_starting_port;
		this.master_port = master_port;
		this.master_ip = master_ip;
		servers = new ArrayList<TCPServer>();
	}
	
	public Main() {
		this(1, 2000, 3000, "127.0.0.1");
	}
	
	public void startAllServers() {
		Thread master = new Thread(() -> {
			try {
				MasterServer m = new MasterServer(master_port);
				servers.add(m);
				m.listen();
			} catch (IOException e) {
				System.out.println("An error occurred when starting server");
				e.printStackTrace();
			}
		});
		master.setDaemon(true);
		master.start();
		try {
			Thread.sleep(1000);

		} catch(InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		for(int i = 0; i < num_slave; i++) {
			final int k = i;
			Thread t = new Thread(() -> {
				try {				
					SlaveServer slave = new SlaveServer(slave_starting_port + k, master_ip, master_port);
					servers.add(slave);
					slave.listen();
				} catch (IOException e) {
					System.out.println("An error occurred in the server");
					e.printStackTrace();
				}
			});
			t.setDaemon(true);
			t.start();
		}
		try {
			Thread.sleep(1000);

		} catch(InterruptedException e) {
			e.printStackTrace();
			return;
		}	
	}
	
	public String getMasterIP() {
		return master_ip;
	}
	
	public int getMasterPort() {
		return master_port;
	}
	
	public void closeAllServers() {
		for(TCPServer server: servers) {
			server.close();
		}
	}
}
