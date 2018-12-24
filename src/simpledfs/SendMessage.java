package simpledfs;

import java.net.*;
import java.io.*;

public class SendMessage {
	
	public static void send(int toPort, byte[] message) throws IOException {
		Thread t1 = new Thread(() ->  {
			try {
				Socket to = new Socket("127.0.0.1", toPort);
				System.out.println("here");
				DataOutputStream out = new DataOutputStream(to.getOutputStream());
				out.write(message);
				out.flush();
				to.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		t1.start();

	}
	
	public static void listen(int port) throws IOException {
		ServerSocket client_listener = new ServerSocket(9090);
        try {
            while(true) {
                Socket socket = client_listener.accept();
                Thread t = new Thread(() -> {
                    try {
                        DataInputStream input =
                                new DataInputStream(socket.getInputStream());
                        socket.close();
//                        PrintWriter out =
//                            new PrintWriter(socket.getOutputStream(), true);
//                        out.println(new Date().toString());
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                });
                t.start();
            }
        }
        finally {
            client_listener.close();
        }
	}
	
	public static void main(String[] args) throws IOException {
		byte[] message1 = {1,2};
		byte[] message2 = {3,4};
		Thread t = new Thread(() -> {
			try {
				listen(9090);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
		System.out.println("here");
		send(9090, message1);
		send(9090,message2);

	}

}
