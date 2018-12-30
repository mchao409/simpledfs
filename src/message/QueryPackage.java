package message;

public class QueryPackage extends MessagePackage {
	private SlaveInfoPackage slave_info;
	
	public QueryPackage(int command) {
		super(command);
	}
	
	public QueryPackage(int command, SlaveInfoPackage slave_info) {
		this(command);
		this.slave_info = slave_info;
	}
	
	public SlaveInfoPackage getSlaveInfo() {
		return slave_info;
	}
	
	public boolean hasSlaveInfo() {
		return slave_info != null;
	}

}
