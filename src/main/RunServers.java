package main;
import java.io.IOException;
import java.util.ArrayList;
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
	public int num_slave;
	public int slave_starting_port;
	public int master_port;
	public String master_ip;
	private HashSet<Integer> slave_ports;
	
	public RunServers() {
		slave_ports = new HashSet<Integer>();
	}
	
	public void start_master_server(String ip, int port) {
		this.master_ip = ip;
		this.master_port = port;
		Thread master = new Thread(() -> {
		try {
			MasterServer m = new MasterServer(master_port);
			m.listen();
		} catch (IOException e) {
			System.out.println("An error occurred in the server");
			e.printStackTrace();
		}
		});
		master.setDaemon(true);
		master.start();
	}
	
	public void start_one_slave_server(int port) {
		if(master_ip == null) {
			System.out.println("Master has not yet started");
			return;
		}
		if(slave_ports.contains(port)) return;
		Thread t = new Thread(() -> {
			try {				
				SlaveServer slave = new SlaveServer(port, master_ip, master_port);
				slave.listen();
				slave_ports.add(port);
			} catch (IOException e) {
				System.out.println("An error occurred in the server");
				e.printStackTrace();
			}
		});
		t.setDaemon(true);
		t.start();
		slave_ports.add(port);
	} 
	

	
	public void start_slave_servers(int starting_port, int num_slaves) throws InterruptedException {
		for(int i = 0; i < num_slaves; i++) {
			start_one_slave_server(starting_port + i);
			Thread.sleep(500);
		}
	}
	
//	public void startAllServers() {
//		Thread master = new Thread(() -> {
//			try {
//				MasterServer m = new MasterServer(master_port);
//				servers.add(m);
//				m.listen();
//			} catch (IOException e) {
//				System.out.println("An error occurred in the server");
//				e.printStackTrace();
//			}
//		});
//		master.setDaemon(true);
//		master.start();
//		try {
//			Thread.sleep(1000);
//
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		for(int i = 0; i < num_slave; i++) {
//			final int k = i;
//			Thread t = new Thread(() -> {
//				try {				
//					SlaveServer slave = new SlaveServer(slave_starting_port + k, master_ip, master_port);
//					servers.add(slave);
//					slave.listen();
//				} catch (IOException e) {
//					System.out.println("An error occurred in the server");
//					e.printStackTrace();
//				}
//			});
//			t.setDaemon(true);
//			t.start();
//		}
//		try {
//			Thread.sleep(1000);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//
//	}
	
	public String getMasterAddress() {
		return master_ip;
	}
	
	public int getMasterPort() {
		return master_port;
	}
}
