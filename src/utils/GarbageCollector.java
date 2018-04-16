package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GarbageCollector {

	private static final int PORT = 4444;
	private static final int TIMEOUT = 3000;

	public static void startreport(String id,String path,int port,AtomicInteger version,boolean isRoot) {
		try {
			MulticastSocket clientSocket = new MulticastSocket(PORT);	
			clientSocket.joinGroup(InetAddress.getByName("226.226.226.226"));
			byte[] requestData = new byte[100];
			DatagramPacket msgPacket = new DatagramPacket(requestData, requestData.length);
			new Thread() {
				public void run() {
					while(true) {
						try {
							clientSocket.receive(msgPacket);

							String asking = new String (msgPacket.getData());
							System.out.println(asking);
							if (asking.trim().equals(id)) {

								String host = String.valueOf(version) + " " + // (version) (uri)
										"http://" 
										+ Inet4Address.getLocalHost().getHostAddress()
										+ ":" + port + path;

								if(isRoot) {
									host += " root";
								}
								System.out.println("Sending " + host);
								//host += path;
								byte[] responseData = host.getBytes();
								//System.out.println("my response " + host);
								DatagramPacket response = new DatagramPacket(responseData, responseData.length,msgPacket.getSocketAddress()) ;

								//System.out.println("source "+ msgPacket.getSocketAddress());

								DatagramSocket uniSocket = new DatagramSocket();

								uniSocket.send(response);
								//uniSocket.close();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}.start();
		}catch(Exception e) {
			System.out.println("Error on report");
		}


	}

	private static String findLatest(String id) {
		System.out.println("into findLatest");
		byte[] requestData = id.getBytes();
		try(DatagramSocket socket = new DatagramSocket()) {
			DatagramPacket request = new DatagramPacket( requestData, requestData.length,InetAddress.getByName("226.226.226.226"),PORT) ;
				socket.send( request ) ;
				long max = -1;
				String message="-1";
				while(true) {
					try {
						socket.setSoTimeout(1000);
						byte[] reply = new byte[100];
						DatagramPacket response = new DatagramPacket( reply,reply.length) ;
						socket.receive(response);
						System.out.println("got List: " + new String(response.getData()).trim());
						long version = Long.valueOf((new String(response.getData()).split(" ")[0]));
						if(version > max || (version == max && message.contains("root"))) {// preference for non-root
							message = new String(response.getData());
							System.out.println("GC: found " + message);
							max = version;
						}
					}catch(SocketTimeoutException e) {
						break;
					}
				}
				socket.close();
				System.out.println("message " + message);
				return message;
		} catch (Exception e) {
			System.out.println("Error getting latest");

		}
		return null;
	}

	public static int findLatestVersion(String id) {
		System.out.println( findLatest(id).trim().split(" ")[0] );
		return Integer.valueOf(findLatest(id).trim().split(" ")[0]);
	}

	public static String findLatestPath(String id) {
		return findLatest(id).trim().split(" ")[1];
	}
}
