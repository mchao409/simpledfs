package message;

import java.io.Serializable;

public abstract class MessagePackage implements Serializable {
	private int command;
	
	public MessagePackage(int command) {
		this.command = command;
	}
	
	public int getCommand() {
		return command;
	}
}
