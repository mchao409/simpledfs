package test;

import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;

class testMasterServer {

	@Test
	void testSuddenDisconnectFromServer() throws IOException, InterruptedException {
		Thread t1 = new Thread(() -> {
			try {
				MasterServer m = new MasterServer(8999);
				m.start();
			} catch (IOException e) {
				System.out.println("An error occurred when starting server");
				e.printStackTrace();
			}
		});
		t1.setDaemon(true);
		t1.start();
		Thread.sleep(1000);
		Socket s = new Socket("127.0.0.1", 8999);
		Thread.sleep(1000);
		s.close();

		Thread.sleep(1000);
		s = new Socket("127.0.0.1", 8999);
		TCPConnection connect = new TCPConnection(s);
		Notify.addFile(connect, new FileContents("testing".getBytes(), "stuff".getBytes()));
		s.close();
		
		
		
		
	}
	
	

}
