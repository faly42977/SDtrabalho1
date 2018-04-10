package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryMulticast {

	final InetAddress group;

	public DiscoveryMulticast () throws UnknownHostException {
		//System.setProperty("java.net.preferIPv4Stack", "true");
		group =  InetAddress.getByName("226.226.226.226") ;	
	}

	public void listen(String service, int port, String path) throws IOException {
		System.out.println("into listen");
		
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
			System.out.println(asking);
			if (asking.trim().equals(service.trim())) {
				
				String host =
						"http://" 
								+ Inet4Address.getLocalHost().getHostAddress()
								+ ":" + port + "/"; //+ path;
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
		System.out.println("into discovery");
		System.out.println("searching for:" + query);
		if( ! group.isMulticastAddress()) {
			//System.out.println( "Not a multicast address (use range : 224.0.0.0 -- 239.255.255.255)");
		}
		//System.out.println(query);
		byte[] requestData = query.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,group,3333) ;
			socket.send( request ) ;
			byte[] reply = new byte[100];
			DatagramPacket response = new DatagramPacket( reply,reply.length) ;
			socket.receive(response);
			socket.close();
			return new String(response.getData()).trim();
		}



	}

	public List<String> findEvery(String query){
		System.out.println("into findEvery");
		ArrayList<String> nodesList = new ArrayList<String>();
		byte[] requestData = query.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,group,3333) ;
			socket.send( request ) ;

			while (true) {
				try {
					socket.setSoTimeout(3000);
					byte[] reply = new byte[100];
					DatagramPacket response = new DatagramPacket( reply,reply.length) ;
					socket.receive(response);
					nodesList.add(new String(response.getData()).trim());
					System.out.println("Got DataNode: " + new String(response.getData()).trim());
				}catch(SocketTimeoutException e) {
					System.out.println("Received all Datanodes");
					break;
				}
			}
			socket.close();

			return nodesList;
		} catch (Exception e) {
			System.out.println("Error getting datanodes");

		}
	return nodesList;	
	}

	}
