package message;

import network.TCPServerInfo;

/**
 * Information about the FileChunk, does not contain chunk data
 *
 */
public class FileChunkInfoPackage extends MessagePackage {
	private String identifier;
	private int start;
	private int end;
	private TCPServerInfo slave;
	
	public FileChunkInfoPackage(String command, String identifier, int start, int end, TCPServerInfo slave) {
		super(command);
		this.identifier = identifier;
		this.start = start;
		this.end = end;
		this.slave = slave;
	}
	
	public String get_identifier() {
		return identifier;
	}
	
	public int get_start() {
		return start;
	}
	
	public int get_end() {
		return end;
	}
	
	public TCPServerInfo get_slave() {
		return slave;
	}
	

}
