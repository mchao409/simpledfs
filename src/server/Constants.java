package server;

public class Constants {
	public static final String[] COMMANDS = {"add", "read", "delete", "new_slave", "client", 
			"print_all", "add_master", "delete_master", "handling_client", "get_all_files", "add_chunk"};
	
	public static final String ADD = "ADD";
	public static final String READ = "READ";
	public static final String DELETE = "DELETE";
	public static final String NEW_SLAVE = "NEW_SLAVE";
	public static final String CLIENT = "CLIENT";
	public static final String PRINT_ALL = "PRINT_ALL";
	public static final String ADD_MASTER = "ADD_MASTER";
	public static final String DELETE_MASTER = "DELETE_MASTER";
	public static final String HANDLING_CLIENT = "HANDLING_CLIENT";
	public static final String GET_ALL_FILES = "GET_ALL_FILES";
	public static final String CHUNK_ADDED = "CHUNK_ADDED";
	
	
	public static final String ADD_SUCCESS = "ADDSUCCESS"; 
	public static final String FILE_ALREADY_EXISTS = "FILEALREADYEXISTS";
	
	public static final String DELETE_SUCCESS = "DELETESUCCESS";
	public static final String FILE_DOES_NOT_EXIST = "FILEDOESNOTEXIST";
	
	public static final String CURRENTLY_HANDLING_CLIENT = "CURRENTLYHANDLINGCLIENT";
	public static final String DONE_HANDLING_CLIENT = "DONEHANDLINGCLIENT";
	
	public static final int CHUNK_SIZE = 200;
		
}
