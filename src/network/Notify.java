package network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import message.QueryPackage;
import message.FileContentsPackage;
import message.TCPServerInfoPackage;

public class Notify {
	private TCPConnection master;
	
	public Notify(String master_address, int master_port) {
		try {
			master = new TCPConnection(new Socket(master_address, master_port));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
		
	private TCPServerInfoPackage query_for_slave() {
		synchronized(master) {
			master.send(new QueryPackage(4));
			try {
				TCPServerInfoPackage slave_info = (TCPServerInfoPackage) master.read();
				
				return slave_info;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public byte[] add_file(FileContents f) {
		try {
			TCPServerInfoPackage slave = query_for_slave();
			TCPServerInfo slave_info = slave.getServerInfo();
			TCPConnection connect = new TCPConnection(new Socket(slave_info.getAddress(), slave_info.getPort()));
			FileContentsPackage m = new FileContentsPackage(0, null, f);
			connect.send(m);
			FileContentsPackage resp = (FileContentsPackage) connect.read();
			return resp.getMessage().getBytes();
		} catch(IOException | NullPointerException e) {
			e.printStackTrace();
			return "An error occurred while adding your file".getBytes();
		}		
	}
	
	public byte[] read_file(String file_name) {
		try {
			TCPServerInfoPackage slave = query_for_slave();
			TCPServerInfo slave_info = slave.getServerInfo();
			TCPConnection connect = new TCPConnection(new Socket(slave_info.getAddress(), slave_info.getPort()));
			connect.send(new FileContentsPackage(1, file_name, null));
			FileContentsPackage resp = (FileContentsPackage) connect.read();
			FileContents file = resp.getFileContents();
			return file.getContents();
		} catch(IOException |NullPointerException  e) {
			e.printStackTrace();
			return "An error occurred and your file could not be read".getBytes();
		}
	}
	
	public byte[] delete_file(String file_name) {
			TCPServerInfoPackage slave = query_for_slave();
			TCPServerInfo slave_info = slave.getServerInfo();
			FileContentsPackage m = new FileContentsPackage(2,file_name, null);
			FileContentsPackage resp;
			try {
				TCPConnection connect = new TCPConnection(new Socket(slave_info.getAddress(), slave_info.getPort()));
				connect.send(m);
				resp = (FileContentsPackage) connect.read();
			} catch(IOException e) {
				e.printStackTrace();
				return "An error occurred while deleting your file".getBytes();
			}
			FileContents file = resp.getFileContents();
			if(file == null) {
				return resp.getMessage().getBytes();
			}
			return file.getContents();
	}
	
	public void printAll(int slave_port) {
		try  {
			TCPConnection connect = new TCPConnection(new Socket("127.0.0.1", slave_port));
			QueryPackage m = new QueryPackage(5);
			connect.send(m);
		} catch(IOException e ) {
			e.printStackTrace();
		}
	}
	
}
