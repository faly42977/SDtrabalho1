package impl.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

import api.storage.Validate;
import utils.GarbageCollector;

public class Namenode implements api.storage.Namenode,api.storage.Validate{

	Trie<String, List<String>> names = new PatriciaTrie<>();
	List<String> removed = new ArrayList<String>();
	AtomicInteger version = new AtomicInteger(0);
	boolean change = false;

	public Namenode() {

		GarbageCollector.startreport("Validate", api.storage.Namenode.PATH, 7777, version,true);
	}


	@Override
	public  synchronized List<String> list(String prefix) {
		System.out.println("list called");
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
		if (keys.isEmpty()) 
			throw new WebApplicationException(404);
		for (String s : keys)
			//System.out.println("no name node nome:" + s);
			if( ! keys.isEmpty() ) {
				
				List<String> ids = new ArrayList<String>();
				for(List<String> part:names.prefixMap( prefix ).values()) {

					ids.addAll(part);
				}
				removed.addAll(ids);
				
				names.keySet().removeAll( keys );
				version.incrementAndGet();
			}
			else
				throw new WebApplicationException(404);
	}
	
		
	
	@Override
	public synchronized void update(String name, List<String> blocks) {
		System.out.println("update called");
		List<String> oldlist = names.get(name);
		List<String> diff = new ArrayList<String>();
		for(String s : oldlist) {
			if(!blocks.contains(s))
				diff.add(s);
		}
		System.out.println(diff);
		removed.addAll(diff);
		if(diff.size()>0) {
			version.incrementAndGet();
		}
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

	@Override
	public List<String> vallist() {
		System.out.println("list called");
		return removed;
	}


}
