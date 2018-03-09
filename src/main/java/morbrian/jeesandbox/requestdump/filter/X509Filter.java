package morbrian.jeesandbox.requestdump.filter;

import java.io.IOException;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by morbrian on 8/23/14.
 */
public class X509Filter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) {
    // nothing to do
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    X509UserPrincipalServletRequestWrapper wrappedRequest = null;
    try {
      wrappedRequest = new X509UserPrincipalServletRequestWrapper((HttpServletRequest) request);
    } catch (LoginException exc) {
      if (response instanceof HttpServletResponse) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, exc.getMessage());
      }
    }
    if (wrappedRequest != null) {
      chain.doFilter(wrappedRequest, response);
    }
  }

  @Override
  public void destroy() {
    // nothing to do
  }
}
