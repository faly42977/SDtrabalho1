package impl.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import utils.Random;

public class Datanode implements api.storage.Datanode {
	

	private static final int INITIAL_SIZE = 32;
	private Map<String, byte[]> blocks = new HashMap<>(INITIAL_SIZE);
	private Map<String, File> files = new HashMap<>(INITIAL_SIZE);
	
	@Override
	public String createBlock(byte[] data) {
		String id = Random.key64();
		blocks.put( id, data);
		try {
			FileOutputStream f = new FileOutputStream("./" + id);
			f.write(data);
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public void deleteBlock(String block) {
		
		//files.remove(block);
		File f = new File("./" + block);
		System.out.println(f.exists());
		
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
