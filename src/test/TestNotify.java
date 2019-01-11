package test;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import main.RunServers;
import network.Notify;
import server.Constants;

class TestNotify {

	@Test
	void test() throws InterruptedException {
		int master_port = 9000;
		int slave_starting_port = 8000;
		int num_slaves = 5;
		RunServers m = new RunServers();
		m.start_master_server("127.0.0.1", master_port);
		m.start_slave_servers(slave_starting_port, num_slaves);
		
		try {
			Thread.sleep(1000); // give time for server to start
			
			File f = new File("src/test/resources/chunkReaderTest.txt");
			byte[] file_contents = Files.readAllBytes(f.toPath());
			Notify n = new Notify("127.0.0.1", master_port);
			n.add_file("testing", "src/test/resources/chunkReaderTest.txt");
			byte[] resp = n.read_file("testing");
			assertTrue(Constants.equalsIgnorePadding(resp,  file_contents));
			
			n.add_file("test1", "src/test/resources/chunkReaderTest.txt");
			
			n.delete_file("testing");

			resp = n.read_file("testing"); 
			assertTrue(resp == null);
			
			n.delete_file("should_not_exist");		
			assertTrue(n.read_file("should_not_exist") == null);
			
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
	}
}
