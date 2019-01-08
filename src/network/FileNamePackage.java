package network;

import message.MessagePackage;

public class FileNamePackage extends MessagePackage {
	
	private String file_identifier;
	
	public FileNamePackage(String command, String file_identifier) {
		super(command);
		this.file_identifier = file_identifier;
	}
	
	public String get_identifier() {
		return file_identifier;
	}
	

}
