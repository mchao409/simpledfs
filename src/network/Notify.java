package network;

import java.io.IOException;
import java.net.Socket;

import message.QueryPackage;
import message.FileContentsPackage;
import message.TCPServerInfoPackage;

/**
 * API for communicating with remote file system
 *
 */
public class Notify {
	private TCPConnection master;
	
	public Notify(String master_address, int master_port) {
		try {
			master = new TCPConnection(new Socket(master_address, master_port));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Query the master for the appropriate slave server to contact.
	 * @return a TCPServerInfo object with information about the slave server
	 */
	private TCPServerInfo query_for_slave() {
		synchronized(master) {
			master.send(new QueryPackage(4));
			try {
				TCPServerInfoPackage slave_info = (TCPServerInfoPackage) master.read();
				
				return slave_info.getServerInfo();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	/**
	 * Add a file to the file system
	 * @param file_name the name of the file on the system
	 * @param contents the contents of the file to be added
	 * @return
	 */
	public byte[] add_file(String file_name, byte[] contents) {
		try {
			FileContents f = new FileContents(file_name.getBytes(), contents);
			TCPServerInfo slave_info = query_for_slave();
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
	
	/**
	 * Read a file from the file system
	 * @param file_name the name of the file to be read
	 * @return a byte array containing the contents of the file, or a byte[] array representing an error message
	 * if an error occurs
	 */
	public byte[] read_file(String file_name) {
		try {
			TCPServerInfo slave_info = query_for_slave();
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
	
	/**
	 * Delete a file from the file system
	 * @param file_name the name of the file to be deleted
	 * @return a byte array containing the contents of the file, or a byte[] array representing an error message
	 * if an error occurs
	 */
	public byte[] delete_file(String file_name) {
			TCPServerInfo slave_info = query_for_slave();
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
	
	/**
	 * Currently used for testing
	 * @param slave_port
	 */
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
