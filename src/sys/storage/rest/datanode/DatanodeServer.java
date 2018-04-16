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
	
		
		String host;
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
			
		} catch (UnknownHostException e) {
			
			return;
		}
		
		String URI_BASE = "http://" + host + ":9999/";

		ResourceConfig config = new ResourceConfig();
		config.register( new Datanode(URI_BASE));

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);

		
		
		new Thread (() ->{
			try {
				DiscoveryMulticast.listen(SERVICE, PORT, api.storage.Datanode.PATH);
			} catch (IOException e) {
	
			}
		}).run();
		
	}
}