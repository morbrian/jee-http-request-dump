package morbrian.jeesandbox.requestdump.x509;

/**
 * When issues encountered while parseing X509 certificates.
 */
public class X509Exception extends Exception {

  public X509Exception(String message) {
    super(message);
  }

  public X509Exception(String message, Throwable cause) {
    super(message, cause);
  }
}
