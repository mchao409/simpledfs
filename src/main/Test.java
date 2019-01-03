package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import network.FileContents;
import network.Notify;

public class Test {

	public static void main(String[] args) throws InterruptedException, IOException {
		Main m = new Main(2, 2000, 3000, "127.0.0.1");
		m.startAllServers();
		Thread.sleep(1000);
		Notify n = new Notify("127.0.0.1", 3000);
		File f = new File("src/test/resources/file2");
		byte[] file_contents = Files.readAllBytes(f.toPath());
		n.add_file("testing", file_contents);
		byte[] resp = n.read_file("testing");
//		System.out.println(new String(resp));
		Thread.sleep(1000);
		n.printAll(2000);
		n.printAll(2001);
		
//		n.delete_file("testing");
//		
//		Thread.sleep(1000);
//		n.printAll(2000);
//		n.printAll(2001);
		
		
	}
}
