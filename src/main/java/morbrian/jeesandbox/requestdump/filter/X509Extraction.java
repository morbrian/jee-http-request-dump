package morbrian.jeesandbox.requestdump.filter;

import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by bmoriarty on 11/20/15.
 */
public class X509Extraction {

  public static String extractCnFromRequest(HttpServletRequest request) {
    return extractCnFromLdif(extractPrimaryLdifFromRequest(request));
  }

  /**
   * Parse the CN string from a DN as formatted in a typical TLS certificate.
   *
   * @param ldif formatted DN expected to include CN stanza
   * @return common name
   */
  public static String extractCnFromLdif(String ldif) {
    String result = "";
    String[] parts = ldif.split(",");
    for (String nvp : parts) {
      String[] pair = nvp.split("=");
      if ("CN".equalsIgnoreCase(pair[0].trim())) {
        result = pair[1].trim();
      }
    }
    return result;
  }

  /**
   * Extract the subject DN from the request object.
   *
   * @param request servlet request.
   * @return subject dn
   */
  public static String extractPrimaryLdifFromRequest(HttpServletRequest request) {
    return extractPrimaryLdif(extractCertChainFromRequest(request));
  }

  /**
   * Extract X509 certificate chain from request.
   *
   * @param request servlet request
   * @return cert chain
   */
  public static X509Certificate[] extractCertChainFromRequest(HttpServletRequest request) {
    if (request == null) {
      return null;
    } else {
      return (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    }
  }

  /**
   * Extract subject DN from certificate chain.
   *
   * @param certObj certificate object
   * @return subject DN formatted string
   */
  public static String extractPrimaryLdif(X509Certificate[] certObj) {
    String def = "";
    if (certObj == null || certObj.length < 1) {
      return def;
    } else {
      Principal subject = certObj[0].getSubjectDN();
      if (subject != null) {
        return subject.getName();
      } else {
        return def;
      }
    }
  }

  /**
   * Produce X509 Identity from servlet request.
   *
   * @param request servlet request
   * @return identity representation of client certificate
   */
  public static X509Identity extractX509IdentityFromRequest(HttpServletRequest request) {
    return new X509Identity(extractPrimaryLdifFromRequest(request));
  }
}
