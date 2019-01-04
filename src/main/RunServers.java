package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import master_server.MasterServer;
import server.TCPServer;
import slave_server.SlaveServer;

/**
 * Main class for starting servers
 *
 */
public class RunServers {
	private int master_port;
	private String master_ip;
	private TCPServer master;
	private HashMap<Integer, TCPServer> slaves;
	
	
	public RunServers() {
		slaves = new HashMap<Integer, TCPServer>();
	}
	
	public void start_master_server(String ip, int port) {
		this.master_ip = ip;
		this.master_port = port;
		Thread master_thread = new Thread(() -> {
		try {
			MasterServer m = new MasterServer(master_port);
			master = m;
			m.listen();
		} catch (IOException e) {
			System.out.println("An error occurred in the server");
			e.printStackTrace();
		}
		});
		master_thread.setDaemon(true);
		master_thread.start();
	}
	
	public void start_one_slave_server(int port) {
		if(master_ip == null) {
			System.out.println("Master has not yet started");
			return;
		}
		if(slaves.keySet().contains(port)) return;
		Thread t = new Thread(() -> {
			try {				
				SlaveServer slave = new SlaveServer(port, master_ip, master_port);
				slave.listen();
				slaves.put(port, slave);
			} catch (IOException e) {
				System.out.println("An error occurred in the server");
				e.printStackTrace();
			}
		});
		t.setDaemon(true);
		t.start();
	} 
	
	public void start_slave_servers(int starting_port, int num_slaves) throws InterruptedException {
		for(int i = 0; i < num_slaves; i++) {
			start_one_slave_server(starting_port + i);
			Thread.sleep(500);
		}
	}
	
	public void closeMaster() {
		master.close();
	}
	
	public void closeSlave(int port) {
		if(slaves.keySet().contains(port)) {
			slaves.get(port).close();
		}
	}
	
	public void closeAllSlaves() {
		for(Integer port : slaves.keySet()) {
			closeSlave(port);
		}
	}
	
	public String getMasterAddress() {
		return master_ip;
	}
	
	public int getMasterPort() {
		return master_port;
	}
}
