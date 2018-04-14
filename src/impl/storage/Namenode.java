package impl.storage;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import utils.GarbageCollector;

public class Namenode implements api.storage.Namenode{

	Trie<String, List<String>> names = new PatriciaTrie<>();
	List<String> removed = new ArrayList<String>();

	public Namenode() {
		new Thread()
		{
			public void run() {
				while(true) {
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						System.out.println("Error on wait");
					}
					GarbageCollector.report(removed);
					removed = new ArrayList<String>();
				}
			}
		}.start();
	}

	public void print() {
		System.out.println("LIST PRINT");
		List<String> l = new ArrayList<>(names.prefixMap("docs-").keySet());
		for(String s:l)
			System.out.println(s);
	}
	@Override
	public  List<String> list(String prefix) {
		System.out.println("list called");
		return new ArrayList<>(names.prefixMap( prefix ).keySet());
	}

	@Override
	public synchronized void  create(String name,  List<String> blocks) {
		print();
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
		for (String s : keys)
			//System.out.println("no name node nome:" + s);
		if( ! keys.isEmpty() ) {
			List<String> ids = new ArrayList<String>();
			for(List<String> part:names.prefixMap( prefix ).values()) {
			//	System.out.println("part: " + part);
				ids.addAll(part);
			}
			removed.addAll(ids);
			names.keySet().removeAll( keys );

		}
		else
			throw new WebApplicationException(404);
	}

	@Override
	public synchronized void update(String name, List<String> blocks) {
		System.out.println("update called");
		if( names.putIfAbsent( name, new ArrayList<>(blocks)) == null ) {
			//logger.info("NOT FOUND");
			System.out.println("update :not found");
			throw new WebApplicationException(404);
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
