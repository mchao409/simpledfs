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
import message.FileContentsPackage;
import message.QueryPackage;
import message.SlaveInfoPackage;
import network.FileContents;
import network.Notify;
import network.TCPConnection;

class TestSlaveServer {

	@Test
	void testConcurrency() throws InterruptedException {
		// start servers
		Main m = new Main(1,2000,3000, "127.0.0.1");
		m.startAllServers();
		
		Thread.sleep(1000);
		byte[] file_contents;
		try {
			Notify n = new Notify("127.0.0.1", 3000);
			// add file
			File f = new File("src/test/resources/file2");
			file_contents = Files.readAllBytes(f.toPath());
			n.add_file(new FileContents("testing".getBytes(), file_contents));
			byte[] resp = n.read_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));

			// Ensure no exceptions
			resp = n.delete_file("should_not_exist");	
			resp = n.read_file("should_not_exist");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		
		// two threads, one attempts to read, other attempts to delete
		Thread read = new Thread(() -> {
			Notify n = new Notify("127.0.0.1", 3000);
			byte[] resp = n.read_file("testing");
		});
		read.start();
		Notify n = new Notify("127.0.0.1", 3000);
		byte[] resp = n.delete_file("testing");
		m.closeAllServers();
	}
	@Test
	void testSeveralSlaves() throws InterruptedException {
		Main m = new Main(2, 2000, 3000, "127.0.0.1");
		m.startAllServers();
		Thread.sleep(1000);
		m.closeAllServers();
	}
}
