package test;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import network.FileContents;
import network.Notify;
import network.TCPConnection;
import server.MasterServer;

class TestNotify {

	@Test
	void test() {
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
		try {
			Thread.sleep(1000); // give time for server to start
			
			TCPConnection connection = new TCPConnection(new Socket("127.0.0.1", 8999));
			File f = new File("src/test/resources/file2");
			byte[] file_contents = Files.readAllBytes(f.toPath());
			Notify.addFile(connection, new FileContents("testing".getBytes(), file_contents));
			byte[] resp = Notify.readFile(connection, "testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			
			resp = Notify.deleteFile(connection, "testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			resp = Notify.readFile(connection, "testing");
			assertFalse(Arrays.equals(resp, file_contents));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
