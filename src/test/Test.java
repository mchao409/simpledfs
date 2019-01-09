package test;

import main.RunServers;
import network.Notify;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		RunServers s = new RunServers();
		s.start_master_server("127.0.0.1", 9000);
		s.start_slave_servers(8000, 3);
		Thread.sleep(500);
		Notify n = new Notify("127.0.0.1", 9000);
		n.add_file("test", "src/test/resources/chunkReaderTest.txt");
		Thread.sleep(1000);
//		n.read_file("test");
		System.out.println(new String(n.read_file("test")));
		n.delete_file("test");
		Thread.sleep(5000);
		
	}
}
