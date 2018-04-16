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

public  class DiscoveryMulticast {



	public static void listen(String service, int port, String path) throws IOException {
		
		
		MulticastSocket clientSocket = new MulticastSocket(3333);
		clientSocket.joinGroup(InetAddress.getByName("226.226.226.226"));

		while (true) {
			byte[] requestData = new byte[100];
			DatagramPacket msgPacket = new DatagramPacket(requestData, requestData.length);
			
			clientSocket.receive(msgPacket);
			String asking = new String (msgPacket.getData());
		
			if (asking.trim().equals(service.trim())) {
				
				String host =
						"http://" 
								+ Inet4Address.getLocalHost().getHostAddress()
								+ ":" + port + "/"; //+ path;
				
				byte[] responseData = host.getBytes();
				
				DatagramPacket response = new DatagramPacket(responseData, responseData.length,msgPacket.getSocketAddress()) ;

			

				DatagramSocket uniSocket = new DatagramSocket();

				uniSocket.send(response);
		

			}
		}

	}


	public static String discover (String query) throws IOException{
		
		byte[] requestData = query.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(3000);
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,InetAddress.getByName("226.226.226.226"),3333) ;
			socket.send( request ) ;
		
			byte[] reply = new byte[100];
			DatagramPacket response = new DatagramPacket( reply,reply.length) ;
			
			socket.receive(response);
	

			socket.close();
			return new String(response.getData()).trim();
		}
		catch (Exception e ) {

			return discover(query);
		}



	}

	public static List<String> findEvery(String query){

		ArrayList<String> nodesList = new ArrayList<String>();
		byte[] requestData = query.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,InetAddress.getByName("226.226.226.226"),3333) ;
			socket.send( request ) ;

			while (true) {
				try {
					socket.setSoTimeout(3000);
					byte[] reply = new byte[100];
					DatagramPacket response = new DatagramPacket( reply,reply.length) ;
					socket.receive(response);
					nodesList.add(new String(response.getData()).trim());
					
				}catch(SocketTimeoutException e) {
					
					break;
				}
			}
			socket.close();

			return nodesList;
		} catch (Exception e) {
			

		}
	return nodesList;	
	}

	}
