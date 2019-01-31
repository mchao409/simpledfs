package network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import file.ChunkReader;
import file.FileChunk;
import file.SystemFile;
import message.QueryPackage;
import message.ChunkLocationPackage;
import message.FileChunkInfoPackage;
import message.FileChunkPackage;
import message.FileInfoPackage;
import message.TCPServerInfoPackage;
import server.Constants;

/**
 * Communication with remote file system. Calls to public methods are not thread-safe.
 *
 */
public class DFS {
	private TCPConnection master;
	
	public DFS(String master_address, int master_port) {
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
	 * Read a file from the dfs and save its contents in a local file
	 * @param file_name the file to read from the dfs
	 * @param path_to_save_to the local path to save the file
	 */
	public void read_file(String file_name, String path_to_save_to) {
		master.send(new FileInfoPackage(Constants.READ_FILE, file_name));
		ChunkLocationPackage resp = (ChunkLocationPackage)master.read();
		HashMap<Integer, List<TCPServerInfo>> chunk_locs = resp.get_chunk_locations();
		if(chunk_locs == null) {
			return;
		}
		Path path = Paths.get(path_to_save_to);
		OutputStream out;
		try {
			out = Files.newOutputStream(path);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("exception");
			return;
		}
		handle_chunk_locs(chunk_locs, file_name, out,null);
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
		handle_chunk_locs(chunk_locs, file_name, null, file);
		return file.get_byte_arr();
	}
	
	/**
	 * Helper method for the `read` method
	 */
	private void handle_chunk_locs(HashMap<Integer, List<TCPServerInfo>> chunk_locs, String file_name,
			OutputStream out, SystemFile file) {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(chunk_locs.keySet());
		Collections.sort(keys);
		for(Integer start: keys) {
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
				if(out != null) {
					try {
						out.write(chunk.get_byte_arr());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					file.add_chunk(chunk);
				}
			}
		}
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
	}	
}
