package test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import main.RunServers;
import network.Notify;

class TestMultipleSlaves {

	@Test
	void testAllSlavesHaveAllFiles() throws IOException {
		int num_slaves = 3;
		RunServers m = new RunServers(num_slaves);
		m.startAllServers();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			fail();
		}
		File f1 = new File("src/test/resources/file2");
		byte[] file_1 = Files.readAllBytes(f1.toPath());
		Notify n = new Notify("127.0.0.1", 3000);
		Thread t1 = new Thread(() -> {
			n.add_file("testing", file_1);
		});
		
		File f2 = new File("src/test/resources/test1");
		byte[] file_2 = Files.readAllBytes(f2.toPath());
		Thread t2 = new Thread(() -> {
			n.add_file("testing1", file_2);
		});
		t1.start();
		t2.start();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			fail();
		}
		
		Thread t3 = new Thread(() -> {
			byte[] resp = n.read_file("testing");
			assertTrue(Arrays.equals(file_1, resp));
			for(int i = 0; i < num_slaves; i++) {
				resp = n.read_file("testing", "127.0.0.1", 2000 + i);
				assertTrue(Arrays.equals(file_1, resp));
			}
			resp = n.delete_file("testing");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
			for(int i = 0; i < num_slaves; i++) {
				resp = n.read_file("testing", "127.0.0.1", 2000 + i);
				assertFalse(Arrays.equals(resp, file_1));
			}
		});
		
		Thread t4 = new Thread(() -> {
			byte[] resp = n.read_file("testing1");
			assertTrue(Arrays.equals(file_2, resp));
			for(int i = 0; i < num_slaves; i++) {
				resp = n.read_file("testing1", "127.0.0.1", 2000 + i);
				assertTrue(Arrays.equals(file_2, resp));
			}
			resp = n.delete_file("testing1");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail();
			}
			for(int i = 0; i < num_slaves; i++) {
				resp = n.read_file("testing1", "127.0.0.1", 2000 + i);
				assertFalse(Arrays.equals(resp, file_2));
			}
		});
		t3.start();
		t4.start();
		
	}

}
