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

import javax.ws.rs.WebApplicationException;

import com.sun.xml.ws.api.message.Packet.Status;

import utils.Random;

public class Datanode implements api.storage.Datanode {


	private static final int INITIAL_SIZE = 32;
	private Map<String, byte[]> blocks = new HashMap<>(INITIAL_SIZE);
	private String path;


	public Datanode(String uri) {
		path = uri + "datanode";
	}

	@Override
	public synchronized String createBlock(byte[] data) {
		String id = Random.key64();
		//blocks.put( id, data);

		try {
			FileOutputStream fos = new FileOutputStream(id) ;
			fos.write(data);
			fos.close();
			blocks.put(id, data);
		} catch (Exception e1) {
			System.out.println("Error_2");

		}

		String r = path + "/" + id;
		//System.out.println("create:" + id);
		return r;

	}


	@Override
	public synchronized void deleteBlock(String block) {
		System.out.println("delete: " + block);
		


		if (!new File(block).exists()) {
			throw new WebApplicationException(404);
		}
		File file = new File(block);
		file.delete();
		blocks.remove(block);

	}

	@Override
	public synchronized byte[] readBlock(String block) {
		byte[] data = null;
		if(blocks.containsKey(block)) {
			System.out.println("read:" + block);
			return blocks.get(block);
		}
		try {
			data = Files.readAllBytes(new File(block).toPath());
		} catch (IOException e) {
			throw new WebApplicationException(404);
		}
		System.out.println("read:" + block);
		return data;

	}
}
