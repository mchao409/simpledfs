package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import main.Main;
import network.Notify;

class TestMultipleSlaves {

	@Test
	void test() throws InterruptedException, IOException {
		int num_slaves = 3;
		Main m = new Main(num_slaves);
		m.startAllServers();
		Thread.sleep(1000);
		
		File f = new File("src/test/resources/file2");
		byte[] file_contents = Files.readAllBytes(f.toPath());
		Notify n = new Notify("127.0.0.1", 3000);
		
		n.add_file("testing", file_contents);
		
		Thread.sleep(500);
		
		byte[] resp = n.read_file("testing");
		assertTrue(Arrays.equals(file_contents, resp));
		
		for(int i = 0; i < num_slaves; i++) {
			resp = n.read_file("testing", "127.0.0.1", 2000 + i);
			assertTrue(Arrays.equals(file_contents, resp));
		}
		
		resp = n.delete_file("testing");
		Thread.sleep(500);
		for(int i = 0; i < num_slaves; i++) {
			resp = n.read_file("testing", "127.0.0.1", 2000 + i);
			assertFalse(Arrays.equals(resp, file_contents));
		}
		
		
		
	}

}
