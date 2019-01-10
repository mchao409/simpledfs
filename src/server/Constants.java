package server;

public class Constants {
	public static final String[] COMMANDS = {"add", "read", "delete", "new_slave", "client", 
			"print_all", "add_master", "delete_master", "handling_client", "get_all_files", "add_chunk"};
	
	public static final String ADD_CHUNK = "ADD_CHUNK";
	public static final String ADD_FILE = "ADD_FILE";
	public static final String READ_CHUNK = "READ_CHUNK";
	public static final String READ_FILE = "READ_FILE";
	public static final String DELETE_FILE = "DELETE_FILE";
	public static final String DELETE_CHUNK = "DELETE_CHUNK";
	public static final String NEW_SLAVE = "NEW_SLAVE";
	public static final String CLIENT = "CLIENT";
	public static final String PRINT_ALL = "PRINT_ALL";
	public static final String ADD_MASTER = "ADD_MASTER";
	public static final String DELETE_MASTER = "DELETE_MASTER";
	public static final String HANDLING_CLIENT = "HANDLING_CLIENT";
	public static final String GET_ALL_FILES = "GET_ALL_FILES";
	public static final String CHUNK_ADDED = "CHUNK_ADDED";
	public static final String CHUNK_DELETED = "CHUNK_DELETED";
	public static final String CHECK_FILE_EXISTS = "CHECK_FILE_EXISTS";
	
	public static final String ADD_SUCCESS = "ADDSUCCESS"; 
	public static final String FILE_ALREADY_EXISTS = "FILEALREADYEXISTS";
	
	public static final String DELETE_SUCCESS = "DELETESUCCESS";
	public static final String FILE_DOES_NOT_EXIST = "FILEDOESNOTEXIST";
	
	public static final String CURRENTLY_HANDLING_CLIENT = "CURRENTLYHANDLINGCLIENT";
	public static final String DONE_HANDLING_CLIENT = "DONEHANDLINGCLIENT";
	
	public static final int CHUNK_SIZE = 200;
	
	/**
	 * Used for testing
	 * Test for equality of two byte arrays, ignoring padding on the end
	 * @return
	 */
	public static boolean equalsIgnorePadding(byte[] arr1, byte[] arr2) {
		if(arr1 == null && arr2 == null) return true;
		if(arr1 == null || arr2 == null) return false;
		int len = Math.min(arr1.length, arr2.length);
		for(int i = 0; i < len; i++) {
			if(arr1[i] != arr2[i]) return false;
		}
		byte[] longer;
		if(arr1.length > arr2.length) longer = arr1;
		else longer = arr2;
		for(int i = len; i < longer.length; i++) {
			if(longer[i] != 0) return false;
		}
		return true;
	}
		
}
