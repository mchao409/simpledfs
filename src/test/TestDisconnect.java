package test;

import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.Test;

import main.RunServers;
import network.DFS;

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
		DFS n = new DFS("127.0.0.1", 9000);

		n.add_file("test", "src/test/resources/chunkReaderTest.txt");

		s.close();
	}
}
