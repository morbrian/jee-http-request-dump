package morbrian.jeesandbox.requestdump.rest;

import java.security.Principal;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// these authorization tags aren't working, not sure why
//@RolesAllowed({"reader", "admin"})
@Path("/public")
@RequestScoped
public class PublicDataRestApi {

  @Inject private Principal principal;
  //@Inject private Logger log;

  @GET
  @Path("/data")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getData() {
    return RestHelper.createResponse("PUBLIC", principal);
  }

}
