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
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("a, r, d");
			String command = scan.next();
			switch(command) {
			case "a":
				promptAddFile();
				break;
			case "r":
				promptReadFile();
				break;
			case "d":
				System.out.println("d");
				break;
			default:
				System.out.println("fail");
			}
		}
	}
	
	private void promptAddFile() {
		System.out.println("File name:");
		Scanner scan = new Scanner(System.in);
		String file_name = scan.nextLine();
		System.out.println("File contents:");
		String contents = scan.nextLine();
		byte[] file_string = (file_name + "\n" + contents).getBytes();
		master_connection.send(merge(new byte[] {0, (byte)file_string.length}, file_string));
		
	}
	
	private void promptReadFile() throws IOException {
		System.out.println("File name: ");
		Scanner scan = new Scanner(System.in);
		String file_name = scan.nextLine().trim();
		byte[] name = file_name.getBytes();
		master_connection.send(merge(new byte[] {1, (byte)name.length}, name));
		int len = master_connection.read();
		byte[] contents = new byte[len];
		master_connection.read(contents, 0, len);
	}
	
	public static byte[] merge(byte[] arr1, byte[] arr2) {
		byte[] merged = new byte[arr1.length + arr2.length];
		for(int i = 0; i < arr1.length; i++) {
			merged[i] = arr1[i];
		}
		for(int i = 0; i < arr2.length; i++) {
			merged[i+arr1.length] = arr2[i];
		}
		return merged;
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