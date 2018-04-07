package impl.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Files;
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
		
			try {
				FileOutputStream fos = new FileOutputStream(id) ;
				fos.write(data);

			} catch (Exception e1) {
				System.out.println("Error_2");

			}

			String r = path + "/" + id;
			return r;
		}
	

	@Override
	public void deleteBlock(String block) {
		System.out.println("delete: " + block);
		blocks.remove(block);
		File file = new File(block);
		file.delete();
	}

	@Override
	public byte[] readBlock(String block) {
		byte[] data = null;
		try {
			data = Files.readAllBytes(new File(block).toPath());
		} catch (IOException e) {
			System.out.println("Error reading");
		}
		return data;
		
	}
}
