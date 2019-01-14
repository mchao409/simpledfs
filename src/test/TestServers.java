package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import main.RunServers;
import network.DFS;
import server.Constants;

class TestServers {

	@Test
	void test() throws IOException {
		int master_port = 9000;
		int slave_port = 8000;
		int num_slaves = 30;
		RunServers r = new RunServers();
		r.start_master_server("127.0.0.1", master_port);
		r.start_slave_servers(slave_port, num_slaves);
		
		DFS n = new DFS("127.0.0.1", master_port);
		n.add_file("file1", "src/test/resources/test_text_1");
		File f = new File("src/test/resources/test_text_1");
		byte[] file_contents = Files.readAllBytes(f.toPath());
		assertTrue(Constants.equalsIgnorePadding(file_contents, n.read_file("file1")));
		
		
		
		
		
		
		
	}

}
