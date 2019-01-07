package file;

/**
 * Represents a chunk of data from a given file
 */
public class FileChunk implements Comparable<FileChunk> {
	/**
	 * The starting byte position of the chunk in the original file
	 */
	private int start;
	/**
	 * The ending byte position of the chunk in the original file
	 */
	private int end;
	/**
	 * A byte-array representing the chunk of data
	 */
	private byte[] chunk;
	
	public FileChunk(int start, int end, byte[] chunk) {
		this.start = start;
		this.end = end;
		this.chunk = chunk;
	}
	
	public int get_len() {
		return chunk.length;
	}
	
	public byte[] get_byte_arr() {
		return chunk;
	}

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
