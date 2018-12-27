package client;
import network.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
	SocketConnection master_connection;
	Scanner scan;
	
	public Client(int masterPort) throws IOException {
		Socket master = new Socket("127.0.0.1", masterPort);
		master_connection = new SocketConnection(master);
		scan = new Scanner(System.in);
		prompt();
	}
	
	private void prompt() throws IOException {
		while(true) {
			System.out.println("Enter 'a' to add a file, 'r' to read a file, 'd' to delete a file.");
			String command = scan.nextLine();
			switch(command) {
			case "a":
				promptAddFile();
				break;
			case "r":
				promptReadFile();
				break;
			case "d":
				promptDeleteFile();
				break;
			default:
				System.out.println("Unrecognized input, please try again");
			}
		}
	}
	
	private void promptAddFile() {
		System.out.println("Enter the path of the file to be added, or ### to go back");
		String path = scan.nextLine();
		if(path.equals("###")) return;
		byte[] file_contents = null;
		try {
			File f = new File("src/test/resources/test1");
			file_contents = Files.readAllBytes(f.toPath());	
		} catch(IOException e) {
			System.out.println("An error occurred when reading the file from your disk");
		}
		
		System.out.println("Enter a valid name for the file on the system, or ### to go back");
		String name = scan.nextLine();
		if(name.equals("###")) return;
		Notify.addFile(master_connection, new FileContents(name.getBytes(), file_contents));
	}
	
	private void promptReadFile() throws IOException {
		System.out.println("Enter the name of the file you would like to read, or ### to go back");
		String file_name = scan.nextLine().trim();
		// TODO how to handle reading?
		System.out.println(new String(Notify.readFile(master_connection, file_name)));
		
	}
	
	private void promptDeleteFile() throws IOException {
		System.out.println("Enter the name of the file you would like to delete, or ### to go back");
		String file_name = scan.nextLine();
		System.out.println(new String(Notify.deleteFile(master_connection, file_name)));

	}
	

    public static void main(String[] args) throws IOException {
    	Client c = new Client(9095);
    }
}