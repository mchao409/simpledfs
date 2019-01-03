package test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import slave_server.SlaveServer;

class TestDisconnect {

	@Test
	void testSuddenDisconnectFromServer() throws IOException, InterruptedException {
		Thread t1 = new Thread(() -> {
			try {
				MasterServer m = new MasterServer(9000);
				m.listen();
			} catch (IOException e) {
				System.out.println("An error occurred when starting server");
				e.printStackTrace();
			}
		});
		t1.setDaemon(true);
		t1.start();		
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		Thread t2 = new Thread(() -> {
			try {
				SlaveServer slave = new SlaveServer(8999, "127.0.0.1", 9000);
				slave.listen();
			} catch(IOException e) {
				System.out.println("An error occurred in the server");
			}
		});
		t2.setDaemon(true);
		t2.start();
		
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
}
