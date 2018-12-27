package server;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {

	
	public static void main(String[] args) throws IOException {
		File f = new File("src/test/resources/test");
		byte[] arr = Files.readAllBytes(f.toPath());
		Files.write(Paths.get("src/test/resources/file2"), arr);
	}
}
