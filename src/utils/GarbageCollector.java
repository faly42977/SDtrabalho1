package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GarbageCollector {
	public static void report(List<String> message) {
		// write to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			DataOutputStream out = new DataOutputStream(baos);
			for (String element : message) {
				out.writeUTF(element);
			}
			byte[] bytes = baos.toByteArray();

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet;

			packet = new DatagramPacket( bytes, bytes.length,InetAddress.getByName("226.226.226.226"),4444);

			socket.send( packet );
			socket.close();
		} catch (Exception e) {
			System.out.println("GarbageCollector: error on report");
		}
	}

	public static List<String> listen() {

		System.out.println("GarbageCollector: into listen");

		//System.out.println("STARTED");
		try {
			MulticastSocket clientSocket = new MulticastSocket(4444);
			clientSocket.joinGroup(InetAddress.getByName("226.226.226.226"));
			byte[] requestData = new byte[100];
			DatagramPacket msgPacket = new DatagramPacket(requestData, requestData.length);
			//System.out.println("multi remote" + clientSocket.getRemoteSocketAddress());
			//System.out.println("multi local" + clientSocket.getLocalSocketAddress());
			clientSocket.receive(msgPacket);
			ByteArrayInputStream bais = new ByteArrayInputStream(msgPacket.getData());
			DataInputStream in = new DataInputStream(bais);
			System.out.println("Garbage listen:");
			List<String>list = new ArrayList<String>();
			while (in.available() > 0) {
			    String element = in.readUTF();
			    list.add(element);
			    System.out.println(element);
			}
			
			return list;
		} catch (Exception e) {
			System.out.println("GarbageCollector: error on listen");
			return null;
		}
	}
}
