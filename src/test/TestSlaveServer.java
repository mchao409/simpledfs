package test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import main.RunServers;
import network.DFS;
import server.Constants;

class TestSlaveServer {

	@Test
	void testConcurrency() throws InterruptedException {
		// start servers
		int master_port = 9000;
		int slave_starting_port = 8000;
		RunServers m = new RunServers();
		m.start_master_server("127.0.0.1", master_port);
		m.start_one_slave_server(slave_starting_port);
		
		Thread.sleep(1000);
		byte[] file_contents;
		try {
			DFS n = new DFS("127.0.0.1", master_port);
			// add file
			File f = new File("src/test/resources/file2");
			file_contents = Files.readAllBytes(f.toPath());
			n.add_file("testing", "src/test/resources/file2");
			byte[] resp = n.read_file("testing");
			assertTrue(Constants.equalsIgnorePadding(resp, file_contents));

			// Ensure no exceptions
			n.delete_file("should_not_exist");	
			resp = n.read_file("should_not_exist");
			assertTrue(resp == null);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return; 
		}
		// two threads, one attempts to read, other attempts to delete
		Thread read = new Thread(() -> {
			DFS n = new DFS("127.0.0.1",master_port);
			n.read_file("testing");

		});
		read.start();
		DFS n = new DFS("127.0.0.1", master_port);
		n.delete_file("testing");

	}
}
