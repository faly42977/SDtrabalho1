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
	
	public void listen(String service, int port) throws IOException {
		while(true) {
			byte[] requestData = new byte[32];
			MulticastSocket socket = new MulticastSocket();
			DatagramPacket request = new DatagramPacket( requestData, requestData.length, group, 3333 ) ;
			socket.receive(request);
			socket.close();
			SocketAddress address = request.getSocketAddress();
			if (String.valueOf(request.getData()).compareTo(service) == 0) {
				String host = Inet4Address.getLocalHost().getHostAddress();
				host += ":" + port;
				byte[] responseData = host.getBytes();
				DatagramPacket response = new DatagramPacket( responseData, responseData.length, 3333 ) ;
				DatagramSocket uniSocket = new DatagramSocket(address);
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
			
		try(MulticastSocket multisocket = new MulticastSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length, group, 3333 ) ;
			multisocket.send( request ) ;    
			multisocket.close();
			DatagramPacket response = new DatagramPacket( responseData, responseData.length, group, 3333 ) ;
			DatagramSocket uniSocket = new DatagramSocket(3333);
			uniSocket.receive(response);
			uniSocket.close();
			return String.valueOf(response.getData()).trim();
		}
	}
	
	




}
