package morbrian.jeesandbox.requestdump.filter;

import morbrian.jeesandbox.requestdump.x509.X509Extraction;

public class X509Identity {

  private static final String ANONYMOUS = "anonymous";
  private static final ThreadLocal<String> SUBJECT_DN = new ThreadLocal<>();

  public X509Identity(String subjectDn) {
    SUBJECT_DN.set(subjectDn);
  }

  public String getCommonName() {
    return X509Extraction.extractCnFromSubjectDn(SUBJECT_DN.get());
  }

  public boolean hasCert() {
    String commonName = getCommonName();
    return commonName != null && !commonName.isEmpty() && !ANONYMOUS.equals(commonName);
  }
}
