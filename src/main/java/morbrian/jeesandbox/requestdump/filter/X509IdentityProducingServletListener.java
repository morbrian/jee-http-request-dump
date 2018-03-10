package morbrian.jeesandbox.requestdump.filter;

import morbrian.jeesandbox.requestdump.x509.X509Exception;
import morbrian.jeesandbox.requestdump.x509.X509Extraction;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

//@WebListener
public class X509IdentityProducingServletListener implements ServletRequestListener {

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
  private X509Identity obtainX509Identity() throws X509Exception {
    return new X509Identity(X509Extraction.extractPrimarySubjectDnFromCert(X509Extraction
        .extractPrimaryCertFromChain(
            X509Extraction.extractCertChainFromRequestAttribute(obtainHttp()))));
  }
}


