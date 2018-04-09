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
		//System.setProperty("java.net.preferIPv4Stack", "true");
		group =  InetAddress.getByName("226.226.226.226") ;	
	}

	public void listen(String service, int port, String path) throws IOException {
		//System.out.println("STARTED");
		MulticastSocket clientSocket = new MulticastSocket(3333);
		clientSocket.joinGroup(group);

		while (true) {
			byte[] requestData = new byte[100];
			DatagramPacket msgPacket = new DatagramPacket(requestData, requestData.length);
			//System.out.println("multi remote" + clientSocket.getRemoteSocketAddress());
			//System.out.println("multi local" + clientSocket.getLocalSocketAddress());
			clientSocket.receive(msgPacket);
			String asking = new String (msgPacket.getData());
			//System.out.println("asked: " + asking);
			//System.out.println("my service:" + service);
			//System.out.println(asking.trim().equals(service.trim()));
			if (asking.trim().equals(service.trim())) {
				//System.out.println("aaa");
				String host =
						"http://" 
								+ Inet4Address.getLocalHost().getHostAddress()
								+ ":" + port + path;
				//host += path;
				byte[] responseData = host.getBytes();
				//System.out.println("my response " + host);
				DatagramPacket response = new DatagramPacket(responseData, responseData.length,msgPacket.getSocketAddress()) ;

				//System.out.println("source "+ msgPacket.getSocketAddress());

				DatagramSocket uniSocket = new DatagramSocket();

				uniSocket.send(response);
				//uniSocket.close();

			}
		}

	}


	public  String discover (String query) throws IOException{

		if( ! group.isMulticastAddress()) {
			//System.out.println( "Not a multicast address (use range : 224.0.0.0 -- 239.255.255.255)");
		}
		//System.out.println(query);
		byte[] requestData = query.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,group,3333) ;
			//System.out.println(request);
			////System.out.println("connected: " + socket.isConnected());
			socket.send( request ) ;
			//System.out.println("sending : " + query);


			byte[] reply = new byte[100];
			DatagramPacket response = new DatagramPacket( reply,reply.length) ;


			socket.receive(response);

			//System.out.println("Port: " + String.valueOf(response.getPort()));
			//System.out.println("Response: " + new String(response.getData()).trim());
			//socket.close();
			return new String(response.getData()).trim();
		}


	}

}
