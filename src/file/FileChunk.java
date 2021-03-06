package file;

import java.io.Serializable;

/**
 * Represents a chunk of data from a given file
 */
public class FileChunk implements Comparable<FileChunk>, Serializable {

	/**
	 * A byte-array representing the chunk of data
	 */
	private byte[] chunk;
	
	/**
	 * The location of the original file that this chunk begins with
	 */
	private int start;
	
//	private ChunkInterval interval;
		
	public FileChunk(int start, byte[] chunk) {
		this.start = start;
//		this.interval = new ChunkInterval(start, end);
		this.chunk = chunk;
	}
	
	public int get_len() {
		return chunk.length;
	}
	
	public byte[] get_byte_arr() {
		return chunk;
	}
	
	public int get_start() {
		return start;
	}
	
//	public int get_end() {
//		return interval.get_end();
//	}

	/**
	 * Returns a negative value if the chunk position of this is before the chunk position of other
	 * Returns a position value if the chunk position of this is after the chunk position of other
	 * Returns 0 if the starting positions are the same
	 */
	@Override
	public int compareTo(FileChunk other) {
		if(start < other.start) return -1;
		if(start > other.start) return 1;
		return 0;
	}
	

}
