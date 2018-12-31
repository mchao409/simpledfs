package test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import main.Main;
import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import slave_server.SlaveServer;

class TestSlaveServer {

	@Test
	void testConcurrency() throws InterruptedException {
		// start servers
		Main m = new Main();
		m.startAllServers();
		
		Thread.sleep(1000);
		byte[] file_contents;
		try {
			Notify n = new Notify();
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
		
		// two threads, one attempts to read, other attempts to delete, ensure no corruption of streams, ensure no printed exceptions
		Thread read = new Thread(() -> {
			Notify n = new Notify();
			byte[] resp = n.read_file("testing");
		});
		read.start();
		Notify n = new Notify();
		byte[] resp = n.delete_file("testing");
	}
}
