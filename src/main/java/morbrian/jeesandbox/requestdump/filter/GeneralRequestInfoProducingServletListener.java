package morbrian.jeesandbox.requestdump.filter;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

//@WebListener
public class GeneralRequestInfoProducingServletListener implements ServletRequestListener {

  private static final ThreadLocal<ServletRequest> SERVLET_REQUESTS = new ThreadLocal<>();

  @Override
  public void requestInitialized(ServletRequestEvent sre) {
    SERVLET_REQUESTS.set(sre.getServletRequest());
  }

  @Override
  public void requestDestroyed(ServletRequestEvent sre) {
    SERVLET_REQUESTS.remove();
  }

  private HttpServletRequest obtainHttp() {
    ServletRequest req = SERVLET_REQUESTS.get();
    return req instanceof HttpServletRequest ? (HttpServletRequest) req : null;
  }

  @Produces
  private GeneralRequestInfo obtainX509Identity() {
    return new GeneralRequestInfo(obtainHttp());
  }
}


