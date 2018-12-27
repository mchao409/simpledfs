package network;

import java.io.IOException;

import simpledfs.FileContents;

public class Notify {

	public static void addFile(SocketConnection s, FileContents f) {
		MessagePackage m = new MessagePackage(0, null, f);
		s.send(m);


	}
	
	public static byte[] readFile(SocketConnection s, String file_name) {
		try {
			MessagePackage m = new MessagePackage(1,file_name, null);
			s.send(m);
			MessagePackage resp = (MessagePackage) s.read();
			FileContents file = resp.getFileContents();
			return file.getContents();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return "An error occured when reading the file".getBytes();
		}
	}
	
	public static byte[] deleteFile(SocketConnection s, String file_name) {
		try {
			MessagePackage m = new MessagePackage(2,file_name, null);
			s.send(m);
			MessagePackage resp = (MessagePackage) s.read();
			FileContents file = resp.getFileContents();
			return file.getContents();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return "An error occurred while deleting the file".getBytes();
		}
	}
	
	public static void sendMessage(SocketConnection s, String msg, int command) {
		MessagePackage m = new MessagePackage(command, msg, null);
		s.send(m);
	}
	
}
