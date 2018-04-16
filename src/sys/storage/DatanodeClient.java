package sys.storage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import api.storage.Datanode;
import utils.DiscoveryMulticast;


public class DatanodeClient implements Datanode {
	private static String service = "Datanode";
	private static Logger logger = Logger.getLogger(Datanode.class.toString() );
	ClientConfig config = new ClientConfig();
	Client client = ClientBuilder.newClient(config);
	URI baseURI;
	WebTarget target;
	
	
	public DatanodeClient (String id) {
		try {
			this.baseURI = new URI(id);
			
			target = client.target( baseURI );
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String createBlock(byte[] data) {
		Response response = target.path("/datanode") 
				.request()
			    .post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

		return response.readEntity(String.class);
	}
	public void deleteBlock(String block) {
		Response response = target.path("/" + block)
				.request()
				.delete();
		
	}

	@Override
	public byte[] readBlock(String block) {
		
		Response response = target.path("/datanode/" + block)
				.request()
				.get();
		
		return response.readEntity(byte[].class);
		
	}
}
