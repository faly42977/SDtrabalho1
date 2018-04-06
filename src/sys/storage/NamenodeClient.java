package sys.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.glassfish.jersey.client.ClientConfig;

import api.storage.Namenode;

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
	URI baseURI = UriBuilder.fromUri("http://127.0.1.1:9999").build();
	WebTarget target = client.target( baseURI );
	
	@Override
	public List<String> list(String prefix) {
		Response response = target.path(Namenode.PATH + "/list/")
			    .queryParam("prefix", prefix)
				.request()
			    .get();
			        
			if( response.hasEntity() ) {
			    List<String> data = response.readEntity(List.class);
			    return data;
			} else {
			    System.err.println( response.getStatus() );
			    return null;
			}
	}

	@Override
	public void create(String name,  List<String> blocks) {
		Response response = target.path(Namenode.PATH + "/" + name)
			    .request()
			    .post(Entity.entity( blocks, MediaType.APPLICATION_JSON));
		System.out.println("create" + name + String.valueOf(response.getStatus()));
	}

	@Override
	public void delete(String prefix) {
		Response response = target.path(Namenode.PATH + "/list/")
			    .queryParam("prefix", prefix)
				.request()
			    .delete();
		System.out.println("delete");
		System.out.println(response.getStatus());
	}

	@Override
	public void update(String name, List<String> blocks) {
		Response response = target.path(Namenode.PATH + "/" + name)
				.request()
			    .put(Entity.entity(blocks, MediaType.APPLICATION_JSON));
		System.out.println("update");
		System.out.println(response.getStatus());
	}

	@Override
	public List<String> read(String name) {
		Response response = target.path(Namenode.PATH + "/" + name)
				.request()
			    .get();
		System.out.println("read");
		System.err.println( response.getStatus() );
		if( response.hasEntity() ) {
		    List<String> data = response.readEntity(List.class);
		    return data;
		} else
		    return null;
	}

}
