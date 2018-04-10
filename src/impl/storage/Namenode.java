package impl.storage;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class Namenode implements api.storage.Namenode{

	Trie<String, List<String>> names = new PatriciaTrie<>();
	
	public Namenode() {
		
	}
	
	public  List<String> list(String prefix) {
		return new ArrayList<>(names.prefixMap( prefix ).keySet());
	}

	@Override
	public synchronized void  create(String name,  List<String> blocks) {
		System.out.println("create called , name: " + name);
		if( names.putIfAbsent(name, new ArrayList<>(blocks)) != null )
			throw new WebApplicationException(409);
		else
			System.out.println("create sucessfull");
	}

	@Override
	public synchronized void delete(String prefix) {
		System.out.println("delete called");
		List<String> keys = new ArrayList<>(names.prefixMap( prefix ).keySet());
		if( ! keys.isEmpty() )
			names.keySet().removeAll( keys );
		else
			throw new WebApplicationException(404);
	}

	@Override
	public synchronized void update(String name, List<String> blocks) {
		System.out.println("update called");
		if( names.putIfAbsent( name, new ArrayList<>(blocks)) == null ) {
			//logger.info("NOT FOUND");
			System.out.println("update :not found");
		}
	}

	@Override
	public synchronized List<String> read(String name) {
		System.out.println("read called");
		List<String> blocks = names.get( name );
		if( blocks == null )
			throw new WebApplicationException(404);
		return blocks;
	}


}
