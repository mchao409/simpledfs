package network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import message.QueryPackage;
import message.FileContentsPackage;
import message.SlaveInfoPackage;

public class Notify {
	private TCPConnection master;
	
//	public Notify(String master_address, int master_port) throws IOException  {
//		master = new TCPConnection(new Socket(master_address, master_port));
//	}
	
	public Notify() {}
	
	private SlaveInfoPackage query_for_slave() {
		if(master == null) {
			try {
				master = new TCPConnection(new Socket("127.0.0.1", 9000));
			} catch(IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		master.send(new QueryPackage(4));
		try {
			SlaveInfoPackage slave_info = (SlaveInfoPackage) master.read();
			
			return slave_info;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] add_file(FileContents f) {
		try {
			SlaveInfoPackage slave = query_for_slave();
			TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
			FileContentsPackage m = new FileContentsPackage(0, null, f);
			connect.send(m);
			FileContentsPackage resp = (FileContentsPackage) connect.read();
			return resp.getMessage().getBytes();
		} catch(IOException | NullPointerException e) {
			return "An error occurred while adding your file".getBytes();
		}		
	}
	
	public byte[] read_file(String file_name) {
		try {
			SlaveInfoPackage slave = query_for_slave();
			TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
			connect.send(new FileContentsPackage(1, file_name, null));
			FileContentsPackage resp = (FileContentsPackage) connect.read();
			FileContents file = resp.getFileContents();
			return file.getContents();
		} catch(IOException |NullPointerException  e) {
			return "An error occurred and your file could not be read".getBytes();
		}
	}
	
	public byte[] delete_file(String file_name) {
			SlaveInfoPackage slave = query_for_slave();
			FileContentsPackage m = new FileContentsPackage(2,file_name, null);
			FileContentsPackage resp;
			try {
				TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
				connect.send(m);
				resp = (FileContentsPackage) connect.read();
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
