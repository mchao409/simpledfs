package network;

import java.io.IOException;

public class Notify {
	

	public static byte[] addFile(TCPConnection s, FileContents f) {
		MessagePackage m = new MessagePackage(0, null, f);
		s.send(m);
		MessagePackage resp;
		try {
			resp = (MessagePackage) s.read();
		} catch(IOException e) {
			return "An error occurred while adding your file".getBytes();
		}
		return resp.getMessage().getBytes();
		
	}
	
	public static byte[] readFile(TCPConnection s, String file_name) {
			MessagePackage m = new MessagePackage(1,file_name, null);
			s.send(m);
			MessagePackage resp;
			try {
				resp = (MessagePackage) s.read();
			} catch(IOException e) {
				return "An error occurred and your file could not be read".getBytes();
			}
			FileContents file = resp.getFileContents();
			return file.getContents();
	}
	
	public static byte[] deleteFile(TCPConnection s, String file_name) {
			MessagePackage m = new MessagePackage(2,file_name, null);
			s.send(m);
			MessagePackage resp;
			try {
				resp = (MessagePackage) s.read();
			} catch(IOException e) {
				return "An error occurred while deleting your file".getBytes();
			}
			FileContents file = resp.getFileContents();
			if(file == null) {
				return resp.getMessage().getBytes();
			}
			return file.getContents();
		
	}
	
}
