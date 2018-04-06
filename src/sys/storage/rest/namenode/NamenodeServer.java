package sys.storage.rest.namenode;

import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import impl.storage.Namenode;

public class NamenodeServer {
	public static void main(String[] args) {
		String host;
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("host: " + host);
		} catch (UnknownHostException e) {
			System.err.println("Cant obtain address");
			return;
		}
		
		String URI_BASE = "http://" + host + ":7777/";

		ResourceConfig config = new ResourceConfig();
		config.register( new Namenode());

		JdkHttpServerFactory.createHttpServer( URI.create(URI_BASE), config);
	}
}
