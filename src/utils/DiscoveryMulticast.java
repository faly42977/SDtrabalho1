package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class DiscoveryMulticast {
	final InetAddress group;

	public DiscoveryMulticast () throws UnknownHostException {
		group =  InetAddress.getByName("226.226.226.226") ;	
	}

	public void listen(String service, int port, String path) throws IOException {
		MulticastSocket clientSocket = new MulticastSocket(3333);
		clientSocket.joinGroup(group);

		while (true) {
			byte[] requestData = new byte[32];
			DatagramPacket msgPacket = new DatagramPacket(requestData, requestData.length);
			clientSocket.receive(msgPacket);
			if (String.valueOf(msgPacket.getData()).compareTo(service) == 0) {
				String host = Inet4Address.getLocalHost().getHostAddress();
				//host += ":" + port + path;
				host += path;
				byte[] responseData = host.getBytes();
				DatagramPacket response = new DatagramPacket( responseData, responseData.length, 3333 ) ;
				DatagramSocket uniSocket = new DatagramSocket(msgPacket.getSocketAddress());
				uniSocket.send(response);
				uniSocket.close();
			}
		}

	}


	public  String discover (String query) throws IOException{

		if( ! group.isMulticastAddress()) {
			System.out.println( "Not a multicast address (use range : 224.0.0.0 -- 239.255.255.255)");
		}

		byte[] requestData = query.getBytes();
		byte[] responseData = new byte[32];

		try(DatagramSocket socket = new DatagramSocket(3333, group)) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length, group, 3333 ) ;
			socket.send( request ) ;    
			socket.close();
			
			DatagramPacket response = new DatagramPacket( responseData, responseData.length, group, 3333 ) ;
			DatagramSocket uniSocket = new DatagramSocket(3333);
			uniSocket.receive(response);
			uniSocket.close();
			return String.valueOf(response.getData()).trim();
		}
	}






}
