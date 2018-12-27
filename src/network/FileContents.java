package network;

import java.io.Serializable;

public class FileContents implements Serializable {
	private byte[] file_name;
	private byte[] contents;
	
	public FileContents(byte[] name, byte[] contents) {
		this.file_name = name;
		this.contents = contents;
	}
	
	public byte[] getContents() {
		return contents;
	}
	
	public byte[] getName() {
		return file_name;
	}

}
