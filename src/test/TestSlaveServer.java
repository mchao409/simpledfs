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

import main.RunServers;
import message.FileContentsPackage;
import message.QueryPackage;
import message.TCPServerInfoPackage;
import network.FileContents;
import network.Notify;
import network.TCPConnection;

class TestSlaveServer {

	@Test
	void testConcurrency() throws InterruptedException {
		// start servers
		RunServers m = new RunServers(1,2000,3000, "127.0.0.1");
		m.startAllServers();
		
		Thread.sleep(1000);
		byte[] file_contents;
		try {
			Notify n = new Notify("127.0.0.1", 3000);
			// add file
			File f = new File("src/test/resources/file2");
			file_contents = Files.readAllBytes(f.toPath());
			n.add_file("testing", file_contents);
			byte[] resp = n.read_file("testing");
			System.out.println("done read");
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
			System.out.println("done read");

		});
		read.start();
		Notify n = new Notify("127.0.0.1", 3000);
		byte[] resp = n.delete_file("testing");
		System.out.println("done delete");

		m.closeAllServers();
	}
	@Test
	void testSeveralSlaves() throws InterruptedException {
		RunServers m = new RunServers(2, 2000, 3000, "127.0.0.1");
		m.startAllServers();
		m.closeAllServers();
	}
}
