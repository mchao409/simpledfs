package file;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a concatenation of several FileChunks
 *
 */
public class SystemFile {
	private List<FileChunk> chunks;
	
	int total_len = 0;
	
	public SystemFile() {
		chunks = new ArrayList<FileChunk>();
	}
	
	/**
	 * Add a FileChunk to the SystemFile
	 */
	public void add_chunk(FileChunk c) {
		chunks.add(c);
		total_len += c.get_len();
	}
	
	/**
	 * Get a byte-array representing the SystemFile
	 */
	public byte[] get_byte_arr() {
		byte[] file = new byte[total_len];
		int curr_ind = 0;
		for(FileChunk c: chunks ) {
			byte[] chunk  = c.get_byte_arr();
			System.arraycopy(chunk, 0, file, curr_ind, chunk.length);
			curr_ind += chunk.length;
		}
		return file;
	}
}
