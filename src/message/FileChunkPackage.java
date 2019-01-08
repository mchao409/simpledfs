package message;

import file.FileChunk;

public class FileChunkPackage extends MessagePackage {
	private String identifier;
	private FileChunk chunk;

	public FileChunkPackage(String command, String identifier, FileChunk chunk) {
		super(command);
		this.identifier = identifier;
		this.chunk = chunk;
	}
	
	public FileChunk get_chunk() {
		return chunk;
	}

	public String get_identifier() {
		return identifier;
	}
}
