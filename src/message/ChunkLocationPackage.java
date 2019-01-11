package message;

import java.util.HashMap;
import java.util.List;

import network.TCPServerInfo;

public class ChunkLocationPackage extends MessagePackage {
	
	/**
	 * Key represents the starting position of a chunk in the file, maps to a list of slave servers containing the chunk
	 */
	private HashMap<Integer, List<TCPServerInfo>> chunk_locs;
	
	/**
	 * Identifier for the file
	 */
	private String identifier;
	
	public ChunkLocationPackage(String command, HashMap<Integer, List<TCPServerInfo>> chunk_locs, String identifier) {
		super(command);
		this.chunk_locs = chunk_locs;
		this.identifier = identifier;
	}
	
	public HashMap<Integer, List<TCPServerInfo>> get_chunk_locations() {
		return chunk_locs;
	}
	
	public String get_identifier() {
		return identifier;
	}
	
}
