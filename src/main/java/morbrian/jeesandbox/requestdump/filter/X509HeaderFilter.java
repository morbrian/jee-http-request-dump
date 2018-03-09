package morbrian.jeesandbox.requestdump.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by morbrian on 8/23/14.
 */
public class X509HeaderFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(X509HeaderFilter.class);

  @Override
  public void init(FilterConfig filterConfig) {
    // nothing to do
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    ServletRequest wrappedRequest = request;
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      Principal principal = httpRequest.getUserPrincipal();
      if (principal != null) {
        X509HeaderServletRequestWrapper x509Request =
            new X509HeaderServletRequestWrapper((HttpServletRequest) request);
        x509Request.setSubjectDn(principal.getName());
        try {
          Class<?> classUser = Class.forName("gidgets.jaas.User");
          Class<?> classRole = Class.forName("gidgets.jaas.Role");
          Method getRoles = classUser.getDeclaredMethod("getRoles");
          List<?> roles = (List) getRoles.invoke(principal);
          Method getName = classRole.getDeclaredMethod("getName");
          for (Object role : roles) {
            x509Request.addRole((String) getName.invoke(role));
          }

        } catch (Exception exc) {
          logger.warn("Failed to apply custom x509 headers", exc);
        }
        wrappedRequest = x509Request;
      }
    }
    chain.doFilter(wrappedRequest, response);
  }

  @Override
  public void destroy() {
    // nothing to do
  }
}
