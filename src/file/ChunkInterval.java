package file;

import java.io.Serializable;

public class ChunkInterval implements Serializable {
	private int start;
	private int end;
	
	public ChunkInterval(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int get_start() {
		return start;
	}
	
	public int get_end() {
		return end;
	}
}
