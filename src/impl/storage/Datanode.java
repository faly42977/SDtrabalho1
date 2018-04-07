package impl.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


import utils.Random;

public class Datanode implements api.storage.Datanode {
	

	private static final int INITIAL_SIZE = 32;
	private Map<String, byte[]> blocks = new HashMap<>(INITIAL_SIZE);
	private String path;
	
	
	public Datanode(String uri) {
		path = uri + "datanode";
	}
	
	@Override
	public String createBlock(byte[] data) {
		String id = Random.key64();
		blocks.put( id, data);
		String r = path + "/" + id;
		return r;
	}

	@Override
	public void deleteBlock(String block) {
		System.out.println("delete: " + block);
		blocks.remove(block);
	}

	@Override
	public byte[] readBlock(String block) {
		byte[] data =  blocks.get(block);
		if( data != null )
			return data;
		else
			throw new RuntimeException("NOT FOUND");
	}
}
