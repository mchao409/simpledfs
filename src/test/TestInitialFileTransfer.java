//package test;
//
//import static org.junit.Assert.assertTrue;
//
//import java.util.Arrays;
//
//import org.junit.jupiter.api.Test;
//
//import main.RunServers;
//import network.Notify;
//
//class TestInitialFileTransfer {
//
//	@Test
//	void test() throws InterruptedException {
//		RunServers s = new RunServers();
//		s.start_master_server("127.0.0.1", 3000);
//		s.start_slave_servers(2000, 1);
//		Notify n = new Notify("127.0.0.1", 3000);
//
//		byte[][] test_contents = {"test1".getBytes(), "test2".getBytes(), "test3".getBytes(), "test4".getBytes()};
//		for(int i = 0; i < test_contents.length; i++) {
//			n.add_file(i + "", test_contents[i]);
//		}
//		
//		s.start_one_slave_server(2001);
//		Thread.sleep(500);
//		
//		for(int i = 0; i < test_contents.length; i++) {
//			byte[] contents = n.read_file(i + "", "127.0.0.1", 2001);
//			assertTrue(Arrays.equals(test_contents[i], contents));
//		}
//		
//	}
//
//}
