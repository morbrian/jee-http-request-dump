package morbrian.jeesandbox.requestdump.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Example of adding headers to servlet request. Calling setUid(..) or setRole(...) adds information
 * to the headers.
 */
public class ModifyHeaderServletRequestWrapper extends HttpServletRequestWrapper {

  final Map<String, List<String>> augmentedHeaders = new HashMap<>();

  /**
   * Constructs a request object wrapping the given request.
   *
   * @throws IllegalArgumentException if the request is null
   */
  public ModifyHeaderServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public void setUid(String uid) {
    augmentedHeaders.put("uid", Collections.singletonList(uid));
  }

  public void setRole(String role) {
    augmentedHeaders.put("role", Collections.singletonList(role));
  }

  @Override
  public String getHeader(String name) {
    return augmentedHeaders.containsKey(name)
        ? augmentedHeaders.get(name).get(0)
        : super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    return augmentedHeaders.containsKey(name)
        ? Collections.enumeration(augmentedHeaders.get(name))
        : super.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    ArrayList<String> list = Collections.list(super.getHeaderNames());
    list.addAll(augmentedHeaders.keySet());
    return Collections.enumeration(augmentedHeaders.keySet());
  }

}
