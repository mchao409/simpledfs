package server.master_server.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import file.ChunkInterval;
import network.TCPServerInfo;
import server.Constants;

/**
 * Contains information about the locations of chunks of a single file (ie. which slave servers the chunks are saved)
 *
 */
public class FileLog {
	
	/**
	 * Each key represents the starting location of the chunk with respect to its file
	 * and maps to a list of servers that contain the chunk
	 */	
	public HashMap<Integer, List<TCPServerInfo>> chunk_locs;
	
	/**
	 * Identifier for the file
	 */
	private String identifier;
	
	/**
	 * Total number of chunks in the file
	 */
	private int num_chunks;
	
	
	public FileLog(String identifier) {
		chunk_locs = new HashMap<Integer, List<TCPServerInfo>>();
		this.identifier = identifier;
	}
	
	/**
	 * Log an added chunk
	 * @param chunk_start the location of the file the chunk begins
	 * @param slave the slave server the chunk was saved to
	 */
	public synchronized void add_chunk_location(int chunk_start, TCPServerInfo slave) {
		if(chunk_locs.get(chunk_start) == null) {
			List<TCPServerInfo> slaves = new ArrayList<TCPServerInfo>();
			chunk_locs.put(chunk_start, slaves);
		}
		if(!chunk_locs.get(chunk_start).contains(slave)) {
			chunk_locs.get(chunk_start).add(slave);
		}
	}
	
	/**
	 * Remove a location of a chunk
	 * @param chunk_start the location of the file the chunk begins
	 * @param slave the slave server the chunk is no longer saved on
	 */
	public synchronized void remove_slave_location(int chunk_start, TCPServerInfo slave) {
		if(chunk_locs.get(chunk_start) != null ) {
			chunk_locs.get(chunk_start).remove(slave);
			if(chunk_locs.get(chunk_start).size() == 0) {
				chunk_locs.remove(chunk_start);
			}
		}
	}
	
	/**
	 * Get the number of chunks that have locations logged
	 */
	public synchronized int get_num_chunks() {
		return chunk_locs.keySet().size();
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
