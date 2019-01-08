package message;

import java.io.Serializable;

public abstract class MessagePackage implements Serializable {
	private String command;
	
	public MessagePackage(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
}
