package network;

import java.io.Serializable;

public class MessagePackage implements Serializable {
	private FileContents file;
	private String message;
	private int command;
//	private int id;
	
	public MessagePackage(int command, String message, FileContents file) {
		this.command = command;
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
	
	public int getCommand() {
		return command;
	}
	
	
	

}
