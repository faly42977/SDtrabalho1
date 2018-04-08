package sys.storage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;



import api.storage.Datanode;
import api.storage.Namenode;
import utils.DiscoveryMulticast;
import utils.Random;

public class DatanodeClient implements Datanode {
	private static String service = "Datanode";
	private static Logger logger = Logger.getLogger(Datanode.class.toString() );
	ClientConfig config = new ClientConfig();
	Client client = ClientBuilder.newClient(config);
	URI baseURI;
	WebTarget target;
	DiscoveryMulticast multicast;
	
	public DatanodeClient () {
	
		try {
			multicast = new DiscoveryMulticast();
			this.baseURI = new URI(multicast.discover("Datanode"));
			System.out.println("baseURI: " + baseURI.getPath());
			target = client.target( baseURI );
		} catch (URISyntaxException e) {
			System.out.println("ERROR2");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR3");
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public String createBlock(byte[] data) {
		Response response = target.path(Datanode.PATH) 
				.request()
			    .post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
		
		return response.readEntity(String.class);
	}
	public void deleteBlock(String block) {
		Response response = target.path(Datanode.PATH)
				.queryParam("block", block)
				.request()
				.delete();
	}

	@Override
	public byte[] readBlock(String block) {
		Response response = target.path(PATH)
				.queryParam("block", block)
				.request()
				.get();
		return response.readEntity(byte[].class);
	}
}
