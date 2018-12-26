package network;

import java.net.*;
import java.io.*;

public class SocketConnection {
//	private String send_ip;
//	private String send_port;
	private Socket socket;
	private DataOutputStream output_stream;
	private DataInputStream input_stream;
	
	public SocketConnection(Socket socket) throws IOException {
//		send_socket = new Socket(ip, toPort);
		this.socket = socket;
		output_stream = new DataOutputStream(socket.getOutputStream());
		input_stream = new DataInputStream(socket.getInputStream());
	}
	
	public boolean send(byte[] message){
		try {
			System.out.println("sending");
				output_stream.write(message);
		} catch(IOException e) {
			return false;
		}
		return true;
	}
	
	public int read() throws IOException {
		return input_stream.read();
	}
	
	public int inputAvailable() throws IOException {
		return input_stream.available();
	}
	
	public void read(byte[] arr, int start, int length) throws IOException {
		input_stream.read(arr,0,length);
	}
	
	
	
//	public static void listen(int port) throws IOException {
//		ServerSocket client_listener = new ServerSocket(9090);
//        try {
//            while(true) {
//                Socket socket = client_listener.accept();
////                String ip = socket.getRemoteSocketAddress().toString();
////                System.out.println(ip);
//                Thread t = new Thread(() -> {
//                    try {
//                        DataInputStream input =
//                                new DataInputStream(socket.getInputStream());
//                        System.out.println(0xff & input.readByte());
//                        socket.close();
////                        PrintWriter out =
////                            new PrintWriter(socket.getOutputStream(), true);
////                        out.println(new Date().toString());
//                    } catch (Exception e) {
//                    	e.printStackTrace();
//                    }
//                });
//                t.start();
//            }
//        }
//        finally {
//            client_listener.close();
//        }
//	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		byte[] message1 = {1,2};
		byte[] message2 = {3,4};
//		SocketConnection m = new SocketConnection("127.0.0.1", 9095);
//		String message = "file_name\ntextextext";
//		byte[] msg = message.getBytes();
//		System.out.println(msg.length);
//		m.send(new byte[] {0, (byte)msg.length});
//		m.send(msg);
//		System.out.println("here");
		Thread.sleep(1000);
//		m.send(new byte[]{5});
//		send("127.0.0.1", 9095, new byte[] {123}, out);

//		Thread.sleep(10000);
//		send("127.0.0.1", 9095, new byte[] {124});

		

	}

}
