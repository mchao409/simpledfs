package message;

import java.io.Serializable;

import network.FileContents;
import network.TCPServerInfo;

// TODO split into two classes - one for client-server communication and one for server-server communication
public class FileContentsPackage extends MessagePackage {
	private FileContents file;
	private String message;
	private TCPServerInfo sender_of_package;
//	private int id;
	
	public FileContentsPackage(String command, String message, FileContents file) {
		super(command);
//		this.id = id;
		this.message = message;
		this.file = file;
	}
	
	public FileContentsPackage(String command, String message, FileContents file, TCPServerInfo sender) {
		this(command, message, file);
		sender_of_package = sender;
	}
	
	public FileContents getFileContents() {
		return file;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void addSender(TCPServerInfo sender) {
		sender_of_package = sender;
	}
	
	public TCPServerInfo getSenderOfPackage() {
		return sender_of_package;
	}
}
