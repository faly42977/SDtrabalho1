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
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.sun.xml.ws.api.message.Packet.Status;

import api.storage.Validate;
import utils.GarbageCollector;
import utils.Random;

public class Datanode implements api.storage.Datanode,api.storage.Validate {


	private static final int INITIAL_SIZE = 32;
	private Map<String, byte[]> blocks = new HashMap<>(INITIAL_SIZE);
	private String path;
	private AtomicInteger myversion = new AtomicInteger(-1);
	private List<String> remove = new ArrayList<String>();

	public Datanode(String uri) {
		path = uri + "datanode";
		System.out.println("initialized");
		startValidation();
		System.out.println("out of thread");
	}



	private void startValidation() {
		GarbageCollector.startreport("Validate", api.storage.Datanode.PATH, 9999, myversion,false);
		new Thread() { 
			public void run() {
				while(true) {
					System.out.println("cycle: "+ myversion.get());
					int outversion = GarbageCollector.findLatestVersion("Validate");
					if(outversion > myversion.get()) {
						System.out.println("Clean called");
						String uri = GarbageCollector.findLatestPath("Validate");
						System.out.println("URI " + uri);
						ClientConfig config = new ClientConfig();
						Client client = ClientBuilder.newClient(config);
						WebTarget target = client.target( uri );
						Response r = target.path("/vallist").request().get();
						if(r.getStatus() == 200) {
							System.out.println("geting valist sucess");
							remove = r.readEntity(List.class);
							System.out.println("removelist = " + remove);
							myversion.set(outversion);
							for(String s : remove) {
								String[] split =  s.split("/");
								String id = split[split.length-1];
								System.out.println(id);
								File f = new File(id);
								if(f.exists()) {
									System.out.println("Found garbage " + id);
									try{
										deleteBlock(id);
									}catch(WebApplicationException e) {
										System.out.println("Error on clean file shoud exist");
									}
								}
							}
						}else {
							System.out.println("error getting valist: " + r.getStatus());
						}
					}
				}

			}
		}.start();
	}


	@Override
	public  String createBlock(byte[] data) {
		String id = Random.key64();


		try {
			FileOutputStream fos = new FileOutputStream(id) ;
			fos.write(data);
			fos.close();
			blocks.put( id, data);
		} catch (Exception e1) {
			System.out.println("Error_2");

		}

		String r = path + "/" + id;
		//System.out.println("create:" + id);
		return r;

	}


	@Override
	public  void deleteBlock(String block) {
		System.out.println("delete: " + block);
		blocks.remove(block);
		if (!new File(block).exists()) {
			throw new WebApplicationException(404);
		}
		File file = new File(block);
		file.delete();

	}

	@Override
	public  byte[] readBlock(String block) {
		byte[] data = null;
		if(blocks.containsKey(block))
			return blocks.get(block);
		try {
			data = Files.readAllBytes(new File(block).toPath());
		} catch (IOException e) {
			throw new WebApplicationException(404);
		}
		System.out.println("read:" + block);
		return data;

	}



	@Override
	public List<String> vallist() {
		return remove;
	}
}
