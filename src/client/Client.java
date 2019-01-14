package client;

import java.io.IOException;
import java.util.Scanner;

import main.RunServers;
import network.DFS;

public class Client {
	Scanner scan;
	private DFS notify;
	public Client(int masterPort) throws IOException {
		scan = new Scanner(System.in);
		notify = new DFS("127.0.0.1", masterPort);
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
		
		System.out.println("Enter a valid name for the file on the distributed file system, or ### to go back");
		String name = scan.nextLine();
		if(name.equals("###")) return;
		notify.add_file(name, path);
		System.out.println("Your file is being added");
	}
	
	private void promptReadFile() throws IOException {
		System.out.println("Enter the name of the file you would like to read, or ### to go back");
		String file_name = scan.nextLine().trim();
		byte[] resp = notify.read_file(file_name);
		if (resp == null) System.out.println("The file could not be found");
		else System.out.println(new String(resp));
		
	}
	
	private void promptDeleteFile() throws IOException {
		System.out.println("Enter the name of the file you would like to delete, or ### to go back");
		String file_name = scan.nextLine();
		notify.delete_file(file_name);
//		byte[] resp = notify.delete_file(file_name);
//		if(resp == null) System.out.println("The file could not be found");
//		else System.out.println(new String(resp));
	}
	

    public static void main(String[] args) {
    	try {
    		RunServers run = new RunServers();
    		run.start_master_server("127.0.0.1", 9000);
    		run.start_slave_servers(8000, 10);
    		Thread.sleep(1000);
        	Client c = new Client(9000);
        	c.prompt();
    	} catch(IOException e) {
    		e.printStackTrace();
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}