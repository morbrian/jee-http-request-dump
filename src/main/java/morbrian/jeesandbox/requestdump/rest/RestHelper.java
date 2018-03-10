package morbrian.jeesandbox.requestdump.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * Created by hikethru08 on 12/20/15.
 */
public class RestHelper {

  /**
   * Adds tag and user name as response data for arbitrary testing.
   *
   * @param tag arbitrary string
   * @param principal principal object
   * @return response with provided data, including user name if available in principal
   */
  public static Response createResponse(String tag, Principal principal) {
    Map<String, String> stuff = new HashMap<>();
    stuff.put("tag", tag);
    try {
      stuff.put("user", (principal != null) ? principal.getName() : "null");
    } catch (Exception exc) {
      stuff.put("error", "can't get username: " + exc.getMessage());
    }
    stuff.put("timestamp", Long.toString(System.currentTimeMillis()));
    return Response.ok(stuff).build();
  }
}
