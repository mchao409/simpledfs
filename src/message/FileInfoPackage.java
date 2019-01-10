package message;

public class FileInfoPackage extends MessagePackage {
	
	private String file_identifier;
	
	private int num_chunks;
	
	public FileInfoPackage(String command, String file_identifier) {
		super(command);
		this.file_identifier = file_identifier;
	}
	
	public FileInfoPackage(String command, String file_identifier, int num_chunks) {
		this(command, file_identifier);
		this.num_chunks = num_chunks;
		
	}
	
	public String get_identifier() {
		return file_identifier;
	}
	
	public int get_num_chunks() {
		return num_chunks;
	}
	

}
