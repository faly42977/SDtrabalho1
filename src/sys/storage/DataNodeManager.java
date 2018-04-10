package sys.storage;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import utils.DiscoveryMulticast;

public class DataNodeManager {
	private DiscoveryMulticast multicast;
	private Map<String, DatanodeClient> datanodes;
	private int DataNodecounter;
	private int blockCounter;
	public DataNodeManager() {
		System.out.println("datanodeMNGR.const");
		try {
			this.multicast = new DiscoveryMulticast();
			this.datanodes = new HashMap<String, DatanodeClient>();
			DataNodecounter = 0;
			blockCounter = 0;
			updateDataNodes();
		} catch (UnknownHostException e) {
			System.out.println("Error creating Multicast");
		}
	}

	public synchronized String createBlock(byte[] data) {
		System.out.println("datanodeMNGR.createBlock");
		int num = (blockCounter % DataNodecounter);
		Object[] nodes  = datanodes.values().toArray();
		blockCounter++;
		return ((DatanodeClient) nodes[num]).createBlock(data);
	}
	
	public synchronized void deleteBlock(String block) {
		System.out.println("datanodeMNGR.deleteBlock");
		String[] division = block.split("/");
		String id = division[division.length - 1];
		String body = block.substring(0, block.length() - id.length() -1);
		datanodes.get(body).deleteBlock(id);
		blockCounter--;
	}
	
	public synchronized byte[] readBlock(String block) {
		System.out.println("datanodeMNGR.readBlock");
		String[] division = block.split("/");
		String id = division[division.length - 1];
		String body = block.substring(0, block.length() - id.length() -1);
		System.out.println("body:" + body + ", id:" + id);
		System.out.println("block<:" + block);
		for (String key :datanodes.keySet())
			System.out.println(key);
		
		
		return datanodes.get(body).readBlock(id);
	}
	
	public synchronized void addDataNode(String nodeId) {
		System.out.println("datanodeMNGR.addDataNode");
		datanodes.putIfAbsent(nodeId+"datanode", new DatanodeClient(nodeId));
	}
	
	public void updateDataNodes() {
		System.out.println("datanodeMNGR.updateDataNodes");
		for ( String nodeId : multicast.findEvery("Datanode")){
			if (!datanodes.containsKey(nodeId) ) {
				addDataNode(nodeId);
				DataNodecounter++;
			}
		}
		
	}
	
}