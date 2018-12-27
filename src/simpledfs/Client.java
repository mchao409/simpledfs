package simpledfs;
import network.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.JOptionPane;

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
//		Scanner scan = new Scanner(System.in);
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
				System.out.println("fail");
			}
		}
	}
	
	private void promptAddFile() {
		System.out.println("File name: ");
		String file_name = scan.nextLine();
		System.out.println("File contents: ");
		String contents = scan.nextLine();
		Notify.addFile(master_connection, new FileContents(file_name.getBytes(), contents.getBytes()));
//		master_connection.send(merge(new byte[] {0, (byte)file_string.length}, file_string));
	}
	
	private void promptReadFile() throws IOException {
		System.out.println("File name: ");
		String file_name = scan.nextLine().trim();
		byte[] name = file_name.getBytes();
		System.out.println(new String(Notify.readFile(master_connection, file_name)));
		
//		master_connection.send(merge(new byte[] {1, (byte)name.length}, name));
//		int len = master_connection.read();
//		byte[] contents = new byte[len];
//		master_connection.read(contents, 0, len);
//		System.out.println(new String(contents));
	}
	
	private void promptDeleteFile() throws IOException {
		System.out.println("File to delete: ");
		String file_name = scan.nextLine();
		System.out.println(new String(Notify.deleteFile(master_connection, file_name)));

//		master_connection.send(merge(new byte[] {2, (byte) name.length}, name));
//		int len = master_connection.read();
//		byte[] contents = new byte[len];
//		master_connection.read(contents, 0, len);
//		System.out.println(new String(contents));

		
	}
	

    public static void main(String[] args) throws IOException {
//        Socket s = new Socket("127.0.0.1", 9091);
//        BufferedReader input =
//            new BufferedReader(new InputStreamReader(s.getInputStream()));
//        String answer = input.readLine();
//        System.out.println(answer);
//        System.out.println("hi");
    	Client c = new Client(9095);
    }
}