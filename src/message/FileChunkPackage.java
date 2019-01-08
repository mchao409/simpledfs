package message;

import file.FileChunk;

public class FileChunkPackage extends MessagePackage {
	private FileChunk chunk;

	public FileChunkPackage(String command, FileChunk chunk) {
		super(command);
		this.chunk = chunk;
	}
	
	public FileChunk getChunk() {
		return chunk;
	}

}
