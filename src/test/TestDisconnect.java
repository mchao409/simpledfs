package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import main.RunServers;
import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import slave_server.SlaveServer;

class TestDisconnect {

	@Test
	void testDisconnect1() throws IOException, InterruptedException {
		RunServers run = new RunServers();
		run.start_master_server("127.0.0.1", 9000);
		run.start_slave_servers(8999,1);
		
		Thread.sleep(1000);
		Socket s = new Socket("127.0.0.1", 8999);
		Thread.sleep(1000);
		s.close();

		Thread.sleep(1000);
		s = new Socket("127.0.0.1", 8999);
		Notify n = new Notify("127.0.0.1", 9000);
		n.add_file("testing", "stuff".getBytes());
		s.close();
	}
	
	@Test
	void testDisconnect2() throws InterruptedException {
		RunServers s = new RunServers();
		int master_port = 3000;
		int slave_starting_port = 2000;
		int num_slaves = 3;
		s.start_master_server("127.0.0.1", master_port);
		s.start_slave_servers(slave_starting_port, num_slaves);
		Notify n = new Notify("127.0.0.1", master_port);
		n.add_file("test1", "test1_contents".getBytes());
		String contents = "testing_contents";
		Thread t1 = new Thread(() -> {
			n.add_file("testing", contents.getBytes(), "127.0.0.1", slave_starting_port);
		});
		t1.start();
		Thread.sleep(100);
		s.closeSlave(slave_starting_port);
		for(int i = 1; i < num_slaves; i++) {
			assertTrue(Arrays.equals(contents.getBytes(), n.read_file("testing", "127.0.0.1", slave_starting_port + i)));
			assertTrue(Arrays.equals("test1_contents".getBytes(), n.read_file("test1","127.0.0.1", slave_starting_port+i)));
		}
	}
	
}
