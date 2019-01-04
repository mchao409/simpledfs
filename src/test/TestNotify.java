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
import master_server.MasterServer;
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import slave_server.SlaveServer;

class TestNotify {

	@Test
	void test() throws InterruptedException {
		int master_port = 9000;
		int slave_starting_port = 8000;
		RunServers m = new RunServers();
		m.start_master_server("127.0.0.1", master_port);
		m.start_one_slave_server(slave_starting_port);
		
		try {
			Thread.sleep(1000); // give time for server to start
			
			File f = new File("src/test/resources/file2");
			byte[] file_contents = Files.readAllBytes(f.toPath());
			Notify n = new Notify("127.0.0.1", master_port);
			n.add_file("testing", file_contents);
			byte[] resp = n.read_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			
			resp = n.delete_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			resp = n.read_file("testing"); 
			assertFalse(Arrays.equals(resp, file_contents));
			
			resp = n.delete_file("testing");
			assertFalse(Arrays.equals(resp, file_contents));

			// Ensure exceptions are handled properly
			resp = n.delete_file("should_not_exist");			
			resp = n.read_file("should_not_exist");
			
			// Repeat with other three methods
			n.add_file("testing", file_contents);
			resp = n.read_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			
			resp = n.delete_file("testing");
			assertTrue(Arrays.equals(resp,  file_contents));
			resp = n.read_file("testing");
			assertFalse(Arrays.equals(resp, file_contents));
			
			resp = n.delete_file("testing");
			assertFalse(Arrays.equals(resp, file_contents));

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
	}
}
