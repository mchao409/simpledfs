package message;

import network.TCPServerInfo;

/**
 * Information about the FileChunk, does not contain chunk data
 *
 */
public class FileChunkInfoPackage extends MessagePackage {
	private String identifier;
	private int start;
	private TCPServerInfo slave;
	
	public FileChunkInfoPackage(String command, String identifier, int start) {
		super(command);
		this.identifier = identifier;
		this.start = start;
	}
	
	
	public FileChunkInfoPackage(String command, String identifier, int start, TCPServerInfo slave) {
		this(command,identifier, start);
		this.identifier = identifier;
		this.start = start;
		this.slave = slave;
	}
	
	public String get_identifier() {
		return identifier;
	}
	
	public int get_start() {
		return start;
	}
	
	public TCPServerInfo get_slave() {
		return slave;
	}
	

}
