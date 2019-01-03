package server;

public class Constants {
	public static final String[] COMMANDS = {"add", "read", "delete", "new_slave", "client", 
			"print_all", "add_master", "delete_master", "handling_client"};
	
	public static final String ADD_SUCCESS = "ADDSUCCESS";
	public static final String FILE_ALREADY_EXISTS = "FILEALREADYEXISTS";
	
	public static final String DELETE_SUCCESS = "DELETESUCCESS";
	public static final String FILE_DOES_NOT_EXIST = "FILEDOESNOTEXIST";
	
	public static final String HANDLING_CLIENT = "HANDLINGCLIENT";
	public static final String DONE_HANDLING_CLIENT = "DONEHANDLINGCLIENT";
		
}
