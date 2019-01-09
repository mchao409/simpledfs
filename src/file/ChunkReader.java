package file;

import java.io.IOException;
import java.io.InputStream;

import server.Constants;

/**
 * Reads in chunks of data from an InputStream
 *
 */
public class ChunkReader {

	private InputStream inFile;
	private int current_pos;
	
	public ChunkReader(InputStream in) {
		inFile = in;
		current_pos = 0;
	}
	
	/**
	 * Returns true if more data is available from the InputStream, false otherwise
	 */
	public boolean available() {
		try {
			return inFile.available() > 0;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Reads in a chunk of a given size
	 * @return the chunk of data, represented by a FileChunk
	 */
	public FileChunk read_chunk()  {
		byte[] data = new byte[Constants.CHUNK_SIZE];
		int num_bytes_read = 0;
		try {
			num_bytes_read = inFile.read(data, 0, Constants.CHUNK_SIZE);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		int start_pos = current_pos;
		current_pos += num_bytes_read;
		if(start_pos == current_pos) {
			return null;
		}
		return new FileChunk(start_pos, data);
	}
}
