package test;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import network.Notify;
import network.SocketConnection;
import server.MasterServer;
import simpledfs.FileContents;

class TestNotify {

	@Test
	void test() {
		Thread t1 = new Thread(() -> {
			try {
				MasterServer m = new MasterServer(8999);
				m.start();
			} catch (IOException e) {
				System.out.println("Error when starting server");
				e.printStackTrace();
			}
		});
		t1.setDaemon(true);
		t1.start();		
		try {
			Thread.sleep(1000);
			SocketConnection connection = new SocketConnection(new Socket("127.0.0.1", 8999));
			Notify.addFile(connection, new FileContents("testing".getBytes(), "contents".getBytes()));
			byte[] resp = Notify.readFile(connection, "testing");
			assert (new String(resp)).equals("contents");
			Notify.deleteFile(connection, "testing");
			
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

}
