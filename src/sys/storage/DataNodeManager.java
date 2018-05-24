package sys.storage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import impl.storage.Namenode;
import utils.DiscoveryMulticast;

public class DataNodeManager {
	private Map<String, DatanodeClient> datanodes;
	private int DataNodecounter;
	private int blockCounter;
	public DataNodeManager() {
		
		try {
			
			this.datanodes = new HashMap<String, DatanodeClient>();
			DataNodecounter = 0;
			blockCounter = 0;
			new Thread (() ->{
				
				try {
			
					updateDataNodes();
				} catch (Exception e) {
					updateDataNodes();
					
				}
			}).run();
			
		} catch (Exception e) {
		
		}
	}

	public synchronized String createBlock(byte[] data) {
		
		int num = (blockCounter % DataNodecounter);
		Object[] nodes  = datanodes.values().toArray();
		blockCounter++;
		return ((DatanodeClient) nodes[num]).createBlock(data);
	}
	
	public synchronized void deleteBlock(String block) {
		
		String[] division = block.split("/");
		String id = division[division.length - 1];
		String body = block.substring(0, block.length() - id.length() -1);
		datanodes.get(body).deleteBlock(id);
		blockCounter--;
	}
	
	public synchronized byte[] readBlock(String block) {
		
		String[] division = block.split("/");
		String id = division[division.length - 1];
		String body = block.substring(0, block.length() - id.length() -1);
		
		
		
		return datanodes.get(body).readBlock(id);
	}
	
	public synchronized void addDataNode(String nodeId) {
		
		datanodes.putIfAbsent(nodeId+"datanode", new DatanodeClient(nodeId));
	}
	
	public synchronized  void updateDataNodes() {
		
		for ( String nodeId : DiscoveryMulticast.findEvery("Datanode")){
			if (!datanodes.containsKey(nodeId) ) {
				addDataNode(nodeId);
				DataNodecounter++;
			}
		}
		
	}
	
}
