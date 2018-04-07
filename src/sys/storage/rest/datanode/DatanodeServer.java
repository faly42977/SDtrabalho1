package sys.storage.rest.datanode;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import impl.storage.Datanode;
import utils.DiscoveryMulticast;


public class DatanodeServer {
	private final static int PORT = 9999;
	private final static String SERVICE = "Datanode";
	public static void main(String[] args) throws UnknownHostException {
	
		DiscoveryMulticast multicast = new DiscoveryMulticast();
		new Thread (() ->{
			try {
				multicast.listen(SERVICE, PORT, Datanode.PATH);
			} catch (IOException e) {
				System.out.println("ERROR ");
				e.printStackTrace();
			}
		}).run();
		
		String host;
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("host: " + host);
		} catch (UnknownHostException e) {
			System.err.println("Cant obtain address");
			return;
		}
		
		String URI_BASE = "http://" + host + ":9999/";

		ResourceConfig config = new ResourceConfig();
		config.register( new Datanode(URI_BASE));

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);
	}
}