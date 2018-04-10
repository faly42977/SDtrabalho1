package sys.mapreduce;
import java.net.Inet4Address;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import api.mapreduce.ComputeNode;
import sys.storage.LocalBlobStorage;


@WebService(serviceName = ComputeNode.NAME, targetNamespace = ComputeNode.NAMESPACE, endpointInterface = ComputeNode.INTERFACE)
public class ComputeNodeServer implements ComputeNode{

	MapReduceEngine mapReduce = new MapReduceEngine("ComputeNode", new LocalBlobStorage());

	@Override
	public void mapReduce(String jobClassBlob, String inputPrefix, String outputPrefix, int outPartSize)
			throws InvalidArgumentException {
		System.out.println("mapReduce called");
		if (jobClassBlob == null || inputPrefix == null || outputPrefix == null)
			throw new InvalidArgumentException();

		else {
			mapReduce.executeJob(jobClassBlob, inputPrefix, outputPrefix, outPartSize);
			//throw new WebApplicationException(404);
		}

	}

	public static void main(String[] args ) {
		try{
			String host = Inet4Address.getLocalHost().getHostAddress();
			String URI_BASE = "http://" + host + ":" + 6666 + ComputeNode.PATH;
			System.out.println("URI:"+ URI_BASE);
			Endpoint.publish(URI_BASE , new ComputeNodeServer());
			System.err.println("SOAP ComputeNodeServer Server ready...");
		}catch (Exception e) {
			System.out.println("Error on thread main - ComputerNodeServer");

		}

	}
}
