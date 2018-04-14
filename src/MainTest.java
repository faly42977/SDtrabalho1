import java.io.IOException;
import java.net.*;
import org.glassfish.jersey.server.*;

import impl.storage.Datanode;
import impl.storage.Namenode;
import utils.DiscoveryMulticast;

import org.glassfish.jersey.jdkhttp.*;

public class MainTest {

	
	
	public static void main(String[] args) throws IOException {
	
	
	System.out.println(DiscoveryMulticast.discover("Namenode"));
	
	/*
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

		String URI_BASE2 = "http://" + host + ":9998/";

		ResourceConfig config2 = new ResourceConfig();
		config2.register( new Datanode());

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE2), config2);
		
		System.err.println("Server ready....");
		*/
	}

}