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
import network.FileContents;
import network.Notify;
import network.TCPConnection;
import server.master_server.MasterServer;
import server.slave_server.SlaveServer;
import server.Constants;

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
			
			File f = new File("src/test/resources/chunkReaderTest.txt");
			byte[] file_contents = Files.readAllBytes(f.toPath());
			Notify n = new Notify("127.0.0.1", master_port);
			n.add_file("testing", "src/test/resources/chunkReaderTest.txt");
			Thread.sleep(500);
			byte[] resp = n.read_file("testing");
			assertTrue(Constants.equalsIgnorePadding(resp,  file_contents));
			
			n.delete_file("testing");
//			assertTrue(Arrays.equals(resp,  file_contents));
			Thread.sleep(500);
			resp = n.read_file("testing"); 
			assertFalse(Constants.equalsIgnorePadding(resp, file_contents));
			
			n.delete_file("testing");

			// Ensure exceptions are handled properly
			n.delete_file("should_not_exist");			
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
