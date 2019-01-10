package network;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import file.ChunkReader;
import file.FileChunk;
import file.SystemFile;
import message.QueryPackage;
import message.ChunkLocationPackage;
import message.FileChunkInfoPackage;
import message.FileChunkPackage;
import message.FileContentsPackage;
import message.FileNamePackage;
import message.TCPServerInfoPackage;
import server.Constants;

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
	
	private List<TCPServerInfo> query_for_slaves() {
		synchronized(master) {
			master.send(new QueryPackage(Constants.CLIENT));
			TCPServerInfoPackage slave_info = (TCPServerInfoPackage) master.read();
			if(slave_info == null) {
				// TODO
			}
			return slave_info.getServers();
		}
	}
	
	/**
	 * Add a file to the distributed file system
	 * @param file_name the name the file will be titled on the system
	 * @param path_to_file local path to file
	 * @return true if successful, false otherwise
	 */
	public boolean add_file(String file_name, String path_to_file) {
		if(read_file(file_name) != null) {
			return false; // File already exists
		}
		BufferedInputStream f;
		try {
			f = new BufferedInputStream(new FileInputStream(path_to_file));
		} catch (FileNotFoundException e) {
			System.out.println("No file could be found at " + path_to_file);
			return false;
		}
		ChunkReader reader = new ChunkReader(f);
		while(reader.available()) {
			FileChunk chunk = reader.read_chunk();
			List<TCPServerInfo> slaves = query_for_slaves();
			for(TCPServerInfo slave_info : slaves) {
				if(slave_info == null) continue;
				try {
					TCPConnection connect = new TCPConnection(new Socket(slave_info.getAddress(), slave_info.getPort()));
					connect.send(new FileChunkPackage(Constants.ADD, file_name, chunk));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	/**
	 * Read a file from the distributed file system
	 * @param file_name
	 * @return a byte array representing the contents of the file
	 */
	public byte[] read_file(String file_name) {
		master.send(new FileNamePackage(Constants.READ, file_name));
		ChunkLocationPackage resp = (ChunkLocationPackage)master.read();
		HashMap<Integer, List<TCPServerInfo>> chunk_locs = resp.get_chunk_locations();
		if(chunk_locs == null) {
			return null;
		}
		SystemFile file = new SystemFile();
		for(Integer start: chunk_locs.keySet()) {
			List<TCPServerInfo> slaves = chunk_locs.get(start);
			FileChunk chunk = null;
			for(int i = 0; i < slaves.size(); i++) {
				if(chunk != null) break;
				TCPServerInfo slave = slaves.get(i);
				try {
					TCPConnection connect = new TCPConnection(new Socket(slave.getAddress(), slave.getPort()));
					connect.send(new FileChunkInfoPackage(Constants.READ, file_name, start));
					FileChunkPackage pkg = (FileChunkPackage)connect.read();
					if(pkg != null) {
						chunk = pkg.get_chunk();
						break;
					}
			
				} catch(IOException e) {
					continue;
				}
			}
			if(chunk != null) {
				file.add_chunk(chunk);
			}
		}
		return file.get_byte_arr();
	}
	
	/**
	 * Delete a file from the file system
	 * @param file_name
	 */
	public void delete_file(String file_name) {
		master.send(new FileNamePackage(Constants.DELETE, file_name));
	}
	
	/**
	 * Currently used for testing
	 * @param slave_port
	 */
	public void printAll(int slave_port) {
		try  {
			TCPConnection connect = new TCPConnection(new Socket("127.0.0.1", slave_port));
			QueryPackage m = new QueryPackage(Constants.PRINT_ALL);
			connect.send(m);
		} catch(IOException e ) {
			e.printStackTrace();
		}
	}
	
}
