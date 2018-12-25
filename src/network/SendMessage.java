package network;

import java.net.*;
import java.io.*;

public class SendMessage {
	private String send_ip;
	private String send_port;
	private Socket send_socket;
	private DataOutputStream currentSocketStream;
	
	public SendMessage(String ip, int toPort) throws IOException {
		send_socket = new Socket(ip, toPort);
		currentSocketStream = new DataOutputStream(send_socket.getOutputStream());
	}
	
	public boolean send(byte[] message){
		try {
			int count = 0;
//			while(count < 50) {
				currentSocketStream.write(message);
				Thread.sleep(100);
//			}
		} catch(IOException e) {
//			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;

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
		SendMessage m = new SendMessage("127.0.0.1", 9095);
		String message = "file_name\ntextextext";
		byte[] msg = message.getBytes();
		System.out.println(msg.length);
		m.send(new byte[] {0, (byte)msg.length});
		m.send(msg);
//		System.out.println("here");
		Thread.sleep(1000);
//		m.send(new byte[]{5});
//		send("127.0.0.1", 9095, new byte[] {123}, out);

//		Thread.sleep(10000);
//		send("127.0.0.1", 9095, new byte[] {124});

		

	}

}
