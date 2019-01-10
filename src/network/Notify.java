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
import message.FileInfoPackage;
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
	
	/**
	 * Query the master for a list of least occupied slaves. The size of the list depends on a constant in MasterServer
	 * @return
	 */
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
		int num_chunks = 0;
		ChunkReader reader = new ChunkReader(f);
		while(reader.available()) {
			FileChunk chunk = reader.read_chunk();
			List<TCPServerInfo> slaves = query_for_slaves();
			for(TCPServerInfo slave_info : slaves) {
				if(slave_info == null) continue;
				try {
					TCPConnection connect = new TCPConnection(new Socket(slave_info.getAddress(), slave_info.getPort()));
					connect.send(new FileChunkPackage(Constants.ADD_CHUNK, file_name, chunk));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			num_chunks++;
		}
		
		boolean ready = false;
		while(!ready) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			master.send(new FileInfoPackage(Constants.IS_FILE_ADDED,file_name, num_chunks));
			QueryPackage resp = (QueryPackage) master.read();
			if(resp.getCommand().equals(Constants.FILE_ADDED)) {
				ready = true;
			}
			
		}
		return true;
	}
	
	/**
	 * Read a file from the distributed file system
	 * @param file_name
	 * @return a byte array representing the contents of the file, null if the file does not exist
	 */
	public byte[] read_file(String file_name) {
		master.send(new FileInfoPackage(Constants.READ_FILE, file_name));
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
					connect.send(new FileChunkInfoPackage(Constants.READ_CHUNK, file_name, start));
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
		master.send(new FileInfoPackage(Constants.DELETE_FILE, file_name));
		boolean deleted = false;
		while(!deleted) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			master.send(new FileInfoPackage(Constants.IS_FILE_DELETED, file_name));
			QueryPackage resp = (QueryPackage)master.read();
			if(resp.getCommand().equals(Constants.FILE_DELETED)) {
				deleted = true;
			}
		}
//		try {
//			Thread.sleep(1000);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//		}
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
