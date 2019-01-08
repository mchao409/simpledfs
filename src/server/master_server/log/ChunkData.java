package server.master_server.log;

import java.util.ArrayList;
import java.util.List;

import file.ChunkInterval;
import network.TCPServerInfo;

public class ChunkData {
	/**
	 * The slave servers that hold this chunk
	 */
	private List<TCPServerInfo> server_locs;
	
	/**
	 * The interval of the entire file that this chunk ranges
	 */
	private ChunkInterval interval;
	
	private String file_identifier;
	

	public ChunkData(String file_identifier, ChunkInterval interval) {
		server_locs = new ArrayList<TCPServerInfo>();
		this.interval = interval;
		this.file_identifier = file_identifier;
	}
	
	public synchronized void add_slave_loc(TCPServerInfo loc) {
		server_locs.add(loc);
	}
	
	public synchronized void remove_slave_loc(TCPServerInfo loc) {
		server_locs.remove(loc);
	}
}
