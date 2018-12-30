package message;

import java.io.Serializable;

import network.FileContents;

public class FileContentsPackage extends MessagePackage {
	private FileContents file;
	private String message;
//	private int id;
	
	public FileContentsPackage(int command, String message, FileContents file) {
		super(command);
//		this.id = id;
		this.message = message;
		this.file = file;
	}
	
	public FileContents getFileContents() {
		return file;
	}
	
	public String getMessage() {
		return message;
	}
}
