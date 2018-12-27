package network;

import java.net.*;
import java.io.*;

public class SocketConnection {
//	private String send_ip;
//	private String send_port;
	private Socket socket;
	private ObjectOutputStream output_stream;
	private ObjectInputStream input_stream;
	
	public SocketConnection(Socket socket) {
		try {
			this.socket = socket;
			output_stream = new ObjectOutputStream(socket.getOutputStream());
			input_stream = new ObjectInputStream(socket.getInputStream());
		} catch(IOException e) {
			System.out.println("An error occurred in SocketConnection");
			e.printStackTrace();
		}

	}
	
	public boolean send(MessagePackage m){
		try {
			output_stream.writeObject(m);
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Object read() throws IOException, ClassNotFoundException {
		try {
			return input_stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int inputAvailable() throws IOException {
		return input_stream.available();
	}
	
//	public void read(byte[] arr, int start, int length) throws IOException {
//		input_stream.read(arr,0,length);
//	}
	
	
	
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
//		byte[] message1 = {1,2};
//		byte[] message2 = {3,4};
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
