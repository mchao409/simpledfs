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
	void testAllSlavesHaveAllFiles() throws IOException, InterruptedException {
//		int num_slaves = 3;
//		int master_port = 9000;
//		int slave_starting_port = 8000;
//		RunServers m = new RunServers();
//		m.start_master_server("127.0.0.1", master_port);
//		m.start_slave_servers(slave_starting_port, num_slaves);
//		
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		File f1 = new File("src/test/resources/file2");
//		byte[] file_1 = Files.readAllBytes(f1.toPath());
//		Notify n = new Notify("127.0.0.1", master_port);
//		Thread t1 = new Thread(() -> {
//			n.add_file("testing", file_1);
//		});
//		
//		File f2 = new File("src/test/resources/test1");
//		byte[] file_2 = Files.readAllBytes(f2.toPath());
//		Thread t2 = new Thread(() -> {
//			n.add_file("testing1", file_2);
//		});
//		t1.start();
//		t2.start();
//
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		
//		Thread t3 = new Thread(() -> {
//			byte[] resp = n.read_file("testing");
//			assertTrue(Arrays.equals(file_1, resp));
//			for(int i = 0; i < num_slaves; i++) {
//				resp = n.read_file("testing", "127.0.0.1", slave_starting_port + i);
//				assertTrue(Arrays.equals(file_1, resp));
//			}
//			resp = n.delete_file("testing");
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				fail();
//			}
//			for(int i = 0; i < num_slaves; i++) {
//				resp = n.read_file("testing", "127.0.0.1", slave_starting_port + i);
//				assertFalse(Arrays.equals(resp, file_1));
//			}
//		});
//		 
//		Thread t4 = new Thread(() -> {
//			byte[] resp = n.read_file("testing1");
//			assertTrue(Arrays.equals(file_2, resp));
//			for(int i = 0; i < num_slaves; i++) {
//				resp = n.read_file("testing1", "127.0.0.1", slave_starting_port + i);
//				assertTrue(Arrays.equals(file_2, resp));
//			}
//			resp = n.delete_file("testing1");
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				fail();
//			}
//			for(int i = 0; i < num_slaves; i++) {
//				resp = n.read_file("testing1", "127.0.0.1", slave_starting_port + i);
//				assertFalse(Arrays.equals(resp, file_2));
//			}
//		});
//		t3.start();
//		t4.start();
	}
	
	@Test
	void testManySlaves() {
		int master_port = 2000;
		int slave_starting_port = 3000;
		RunServers s = new RunServers();
		s.start_master_server("127.0.0.1", master_port);
		s.start_slave_servers(slave_starting_port, 10);
		Notify n = new Notify("127.0.0.1", master_port);
		n.add_file("test1", "test1_contents".getBytes());
		for(int i = 0; i < 10; i++) {
			assertTrue(Arrays.equals("test1_contents".getBytes(), n.read_file("test1", "127.0.0.1", slave_starting_port + i)));
		}
		n.add_file("test2", "test2_contents".getBytes());
		for(int i = 0; i < 10; i++) {
			assertTrue(Arrays.equals("test2_contents".getBytes(), n.read_file("test2", "127.0.0.1", slave_starting_port + i)));
		}
		s.start_slave_servers(slave_starting_port + 10, 10);
		for(int i = 0; i < 20; i++) {
			assertTrue(Arrays.equals("test1_contents".getBytes(), n.read_file("test1", "127.0.0.1", slave_starting_port + i)));
			assertTrue(Arrays.equals("test2_contents".getBytes(), n.read_file("test2", "127.0.0.1", slave_starting_port + i)));
		}
		n.delete_file("test1");
		s.start_slave_servers(slave_starting_port + 20, 10);
		
		for(int i = 0; i < 30; i++) {
			assertFalse(Arrays.equals("test1_contents".getBytes(), n.read_file("test1", "127.0.0.1", slave_starting_port + i)));
			assertTrue(Arrays.equals("test2_contents".getBytes(), n.read_file("test2", "127.0.0.1", slave_starting_port + i)));

		}
	}

}
