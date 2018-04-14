package sys.storage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import api.storage.Namenode;
import utils.DiscoveryMulticast;

/*
 * Fake NamenodeClient client.
 * 
 * Rather than invoking the Namenode via REST, executes
 * operations locally, in memory.
 * 
 * Uses a trie to perform efficient prefix query operations.
 */
public class NamenodeClient implements Namenode {




	ClientConfig config = new ClientConfig();
	Client client = ClientBuilder.newClient(config);
	URI baseURI;
	WebTarget target;

	public NamenodeClient () {
		System.out.println("Entry on namenodeclient");
		try {
			try {
				String uri =  DiscoveryMulticast.discover("Namenode");
				this.baseURI = new URI(uri);
				System.out.println("built a uri:" + uri);
			}catch (Exception e) {

				System.out.println("error building uri:");

			}
			System.out.println("baseURI: " + baseURI.toString());
			target = client.target( baseURI );
		} catch (Exception e) {
			System.out.println("ERROR2");
			e.printStackTrace();
		}
	}

	@Override
	public List<String> list(String prefix) {
		System.out.println("TRYING " + prefix);
		Response response = target.path("/namenode/list")
				.queryParam("prefix", prefix)
				.request()
				.get();

		if( response.hasEntity() ) {
			List<String> data = response.readEntity(List.class);
			return data;
		} else {
			System.out.println(target.path("/list/").queryParam("prefix", prefix).getUri());
			System.err.println( response.getStatus() );
			return new ArrayList<String>();
		}
	}

	@Override
	public void create(String name,  List<String> blocks) {
		Response response = target.path("/namenode/" + name)
				.request()
				.post(Entity.entity( blocks, MediaType.APPLICATION_JSON));
		System.out.println("create" + name + String.valueOf(response.getStatus()));
	}

	@Override
	public void delete(String prefix) {
		Response response = target.path("/namenode/list")
				.queryParam("prefix", prefix)
				.request()
				.delete();
		System.out.println("delete");
		System.out.println(response.getStatus());
	}

	@Override
	public void update(String name, List<String> blocks) {
		Response response = target.path("/namenode/" + name)
				.request()
				.put(Entity.entity(blocks, MediaType.APPLICATION_JSON));
		System.out.println("update");
		System.out.println(response.getStatus());
	}

	@Override
	public List<String> read(String name) {
		Response response = target.path("/namenode/" + name)
				.request()
				.get();
		System.out.println("read");
		System.err.println( response.getStatus() );
		System.out.println("STATUS RECVD:" + response.getStatus());
		if( response.getStatus()==200 ) {
			List<String> data = response.readEntity(List.class);
			if (data == null)
				System.out.println("DATA NULL");
			return data;
		} else
			return new ArrayList<String>();
	}

}
