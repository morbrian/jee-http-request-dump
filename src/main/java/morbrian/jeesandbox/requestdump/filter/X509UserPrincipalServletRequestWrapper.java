package morbrian.jeesandbox.requestdump.filter;

import morbrian.jeesandbox.requestdump.x509.X509Exception;
import morbrian.jeesandbox.requestdump.x509.X509Extraction;

import java.security.Principal;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class X509UserPrincipalServletRequestWrapper extends HttpServletRequestWrapper {

  // private Principal principal;

  public X509UserPrincipalServletRequestWrapper(HttpServletRequest request) throws LoginException {
    super(request);
    loginToSetPrincipal(request);
  }

  private void loginToSetPrincipal(HttpServletRequest request) throws LoginException {
    if (request == null) {
      throw new LoginException("Request object null, login impossible");
    }

    //    String username = X509Extraction.extractCnFromRequest(request);
    String username;
    try {
      username = X509Extraction.extractPrimarySubjectDnFromCert(X509Extraction
          .extractPrimaryCertFromChain(
              X509Extraction.extractCertChainFromRequestAttribute(request)));
    } catch (X509Exception exc) {
      throw new LoginException("cannot extract username from X509: " + exc.getMessage());
    }

    //username = "dev_moore";
    // fail if no user can be identified
    if (username == null) {
      throw new LoginException("No identity in certificate, unable to authenticate.");
    }

    Principal superPrincipal = super.getUserPrincipal();

    // if principal not set yet, or principal is different than current PKI certificate
    if (superPrincipal == null || !username.equals(superPrincipal.getName())) {
      request.getSession();
      try {
        request.login(username, "changeme");
      } catch (ServletException exc) {
        throw new LoginException("Login failed for " + username);
      }
    }
  }
}
