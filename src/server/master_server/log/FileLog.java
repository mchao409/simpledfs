package server.master_server.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import file.ChunkInterval;
import network.TCPServerInfo;
import server.Constants;

public class FileLog {
	
	/**
	 * Each key represents the starting location of the chunk with respect to its file
	 * and maps to a list of servers that contain the chunk
	 */	
	private HashMap<Integer, List<TCPServerInfo>> chunk_locs;
	
	/**
	 * Identifier for the file
	 */
	private String identifier;
	
	
	public FileLog(String identifier) {
		chunk_locs = new HashMap<Integer, List<TCPServerInfo>>();
		this.identifier = identifier;
	}
	
	public synchronized void add_chunk_location(int chunk_start, TCPServerInfo slave) {
		if(chunk_locs.get(chunk_start) == null) {
			List<TCPServerInfo> slaves = new ArrayList<TCPServerInfo>();
			chunk_locs.put(chunk_start, slaves);
		}
		if(!chunk_locs.get(chunk_start).contains(slave)) {
			chunk_locs.get(chunk_start).add(slave);
		}
	}
	
	public synchronized void remove_slave_location(int chunk_start, TCPServerInfo slave) {
		if(chunk_locs.get(chunk_start) != null ) {
			chunk_locs.get(chunk_start).remove(slave);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof FileLog)) return false;
		return identifier.equals(((FileLog)other).identifier);
	}
	
	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
}
