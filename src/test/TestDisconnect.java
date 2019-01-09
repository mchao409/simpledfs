package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import main.RunServers;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import server.master_server.MasterServer;
import server.slave_server.SlaveServer;

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

		n.add_file("test", "src/test/resources/chunkReaderTest.txt");

		s.close();
	}
}
