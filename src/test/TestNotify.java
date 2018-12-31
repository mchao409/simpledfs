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

import main.Main;
import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import slave_server.SlaveServer;

class TestNotify {

	@Test
	void test() throws InterruptedException {
//		Thread t1 = new Thread(() -> {
//			try {
//				MasterServer m = new MasterServer(9000);
//				m.listen();
//			} catch (IOException e) {
//				System.out.println("An error occurred when starting server");
//				e.printStackTrace();
//			}
//		});
//		t1.setDaemon(true);
//		t1.start();		
//		try {
//			Thread.sleep(1000);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		Thread t2 = new Thread(() -> {
//			try {
//				SlaveServer slave = new SlaveServer(8999, "127.0.0.1", 9000);
//				slave.listen();
//			} catch(IOException e) {
//				System.out.println("An error occurred in the server");
//			}
//		});
//		t2.setDaemon(true);
//		t2.start();
		Main m = new Main();
		m.startAllServers();
		
		try {
			Thread.sleep(1000); // give time for server to start
			
			File f = new File("src/test/resources/file2");
			byte[] file_contents = Files.readAllBytes(f.toPath());
			Notify n = new Notify();
			n.add_file(new FileContents("testing".getBytes(), file_contents));
			byte[] resp = n.read_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			
			resp = n.delete_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			resp = n.read_file("testing");
			assertFalse(Arrays.equals(resp, file_contents));
			
			// Ensure exceptions are handled properly
			resp = n.delete_file("should_not_exist");			
			resp = n.read_file("should_not_exist");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
	}
}
