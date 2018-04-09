package impl.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class Namenode implements api.storage.Namenode{

	Trie<String, List<String>> names = new PatriciaTrie<>();
	
	public Namenode() {
		
	}
	
	public List<String> list(String prefix) {
		return new ArrayList<>(names.prefixMap( prefix ).keySet());
	}

	@Override
	public void create(String name,  List<String> blocks) {
		System.out.println("create called");
		if( names.putIfAbsent(name, new ArrayList<>(blocks)) != null )
			//logger.info("CONFLICT");
			System.out.println("create error");
		else
			System.out.println("create sucessfull");
	}

	@Override
	public void delete(String prefix) {
		List<String> keys = new ArrayList<>(names.prefixMap( prefix ).keySet());
		if( ! keys.isEmpty() )
			names.keySet().removeAll( keys );
	}

	@Override
	public void update(String name, List<String> blocks) {
		if( names.putIfAbsent( name, new ArrayList<>(blocks)) == null ) {
			//logger.info("NOT FOUND");
			System.out.println("update :not found");
		}
	}

	@Override
	public List<String> read(String name) {
		System.out.println("list called");
		List<String> blocks = names.get( name );
		//if( blocks == null )
			//logger.info("NOT FOUND");
		return blocks;
	}


}
