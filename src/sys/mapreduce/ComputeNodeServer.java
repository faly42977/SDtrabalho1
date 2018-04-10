package sys.mapreduce;
import java.net.Inet4Address;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.xml.ws.Endpoint;

import api.mapreduce.ComputeNode;


@WebService(serviceName = ComputeNode.NAME, targetNamespace = ComputeNode.NAMESPACE, endpointInterface = ComputeNode.INTERFACE)
public class ComputeNodeServer implements ComputeNode{

	@Override
	public void mapReduce(String jobClassBlob, String inputPrefix, String outputPrefix, int outPartSize)
			throws InvalidArgumentException {
			
		if (jobClassBlob == null || inputPrefix == null || outputPrefix == null)
			throw new InvalidArgumentException();
		
		else {
			throw new WebApplicationException(404);
		}
		

	}
	
	  public static void main(String[] args ) {
		  try {
	        String host = Inet4Address.getLocalHost().getHostAddress();
	        String baseURI = "http://" + host + ":6666/";
	        System.out.println("URI:"+ baseURI);
	        Endpoint.publish(baseURI , new ComputeNodeServer());
	        System.err.println("SOAP DatanodeServer Server ready...");
		  } catch (Exception e) {
			  System.out.println("Error on thread main - ComputerNodeServer");
		  }
	    }

}
