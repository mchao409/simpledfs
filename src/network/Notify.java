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
			TCPServerInfoPackage slave_info = (TCPServerInfoPackage) master.read();
			if(slave_info == null) {
				// TODO
			}
			return slave_info.getServerInfo();
		
		}
	}
	
	/**
	 * Add file to the file system to an unspecified slave
	 * @param file_name the name of the file to be added to the system
	 * @param contents the contents of the file to be added
	 * @return contents of the file if it was successfully added, or an error message
	 */
	public byte[] add_file(String file_name, byte[] contents) {
		TCPServerInfo slave_info = query_for_slave();
		return add_file(file_name, contents, slave_info.getAddress(), slave_info.getPort());
	}
	
	/**
	 * Add a file to the file system using a specified slave 
	 * @param file_name the name of the file to be added to the system
	 * @param contents the contents of the file to be added
	 * @param slave_address the address of the slave server
	 * @param slave_port the port of the slave server
	 * @return contents of the file if it was successfully added, or an error message
	 */
	public byte[] add_file(String file_name, byte[] contents, String slave_address, int slave_port) {
		try {
			FileContents f = new FileContents(file_name.getBytes(), contents);
			TCPConnection connect = new TCPConnection(new Socket(slave_address, slave_port));
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
		TCPServerInfo slave_info = query_for_slave();
		return read_file(file_name, slave_info.getAddress(), slave_info.getPort());
	}
	
	/**
	 * Read a file from the file system
	 * @param file_name the name of the file to be read
	 * @return a byte array containing the contents of the file, or a byte[] array representing an error message
	 * if an error occurs
	 */
	public byte[] read_file(String file_name, String slave_address, int slave_port) {
		try {
			TCPConnection connect = new TCPConnection(new Socket(slave_address, slave_port));
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
		TCPServerInfo slave_info = query_for_slave();
		return delete_file(file_name, slave_info.getAddress(), slave_info.getPort());
	}
	
	/**
	 * Delete a file from the file system
	 * @param file_name the name of the file to be deleted
	 * @return a byte array containing the contents of the file, or a byte[] array representing an error message
	 * if an error occurs
	 */
	public byte[] delete_file(String file_name, String slave_address, int slave_port) {
			FileContentsPackage m = new FileContentsPackage(2,file_name, null);
			FileContentsPackage resp;
			try {
				TCPConnection connect = new TCPConnection(new Socket(slave_address, slave_port));
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
