package sys.storage.rest.namenode;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import api.storage.Datanode;
import impl.storage.Namenode;
import utils.DiscoveryMulticast;

public class NamenodeServer {
	private static final int PORT = 7777;
	private static final String SERVICE = "Namenode";
	public static void main(String[] args) throws UnknownHostException {
		
		
		
		
		String host;
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("host: " + host);
		} catch (UnknownHostException e) {
			System.err.println("Cant obtain address");
			return;
		}
		
		String URI_BASE = "http://" + host + ":" + PORT +"/";

		ResourceConfig config = new ResourceConfig();
		config.register( new Namenode());

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);
		System.out.println("URI " + URI_BASE);
		
		new Thread (() ->{
			try {
				DiscoveryMulticast.listen(SERVICE, PORT, api.storage.Namenode.PATH);
			} catch (IOException e) {
				System.out.println("ERROR ");
				e.printStackTrace();
			}
		}).run();
	}
}
