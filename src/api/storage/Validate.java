package api.storage;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//@Path(Validate.PATH)
public interface Validate {
	
	public static final String PATH = "/validate" ;
	
	@GET
	@Path("/vallist")
	@Produces(MediaType.APPLICATION_JSON)
	List<String> vallist();
}
