package main;
import java.io.IOException;

import master_server.MasterServer;
import slave_server.SlaveServer;

public class Main {
	public int num_slave;
	public int slave_starting_port;
	public int master_port;
	public String master_ip;
	
	public Main(int num_slave, int slave_starting_port, int master_port, String master_ip) {
		this.num_slave = num_slave;
		this.slave_starting_port = slave_starting_port;
		this.master_port = master_port;
		this.master_ip = master_ip;
	}
	
	public Main() {
		this(1, 10000, 9000, "127.0.0.1");
	}
	
	public void startAllServers() throws InterruptedException {
		Thread master = new Thread(() -> {
			try {
				MasterServer m = new MasterServer(master_port);
				m.listen();
			} catch (IOException e) {
				System.out.println("An error occurred when starting server");
				e.printStackTrace();
			}
		});
		master.setDaemon(true);
		master.start();
		
		Thread.sleep(1000);
		
		for(int i = 0; i < num_slave; i++) {
			final int k = i;
			Thread t = new Thread(() -> {
				try {				
					SlaveServer slave = new SlaveServer(slave_starting_port + k, master_ip, master_port);
					slave.listen();
				} catch (IOException e) {
					System.out.println("An error occurred in the server");
					e.printStackTrace();
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}
}
