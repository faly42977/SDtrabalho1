import java.io.IOException;
import java.net.*;
import org.glassfish.jersey.server.*;

import impl.storage.Datanode;
import impl.storage.Namenode;
import utils.DiscoveryMulticast;

import org.glassfish.jersey.jdkhttp.*;

public class MainTest {

	
	
	public static void main(String[] args) throws IOException {
	
	
		String host;
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("host: " + host);
		} catch (UnknownHostException e) {
			System.err.println("Cant obtain address");
			return;
		}
		
		String URI_BASE = "http://" + host +":9999/";

		ResourceConfig config = new ResourceConfig();
		config.register( new Namenode());

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);

	}

}