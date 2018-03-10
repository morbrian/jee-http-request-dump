package morbrian.jeesandbox.requestdump.x509;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;
import javax.mail.internet.InternetAddress;

public class X509ExtractionTest {
  private static final String EXPECTED_FULL_CN = "TARGARYEN.DAENERYS.MIDDLE.1234567890";
  private static final String EXPECTED_NO_MIDDLE_CN = "TARGARYEN.DAENERYS.1234567890";
  private static final String EXPECTED_FIRST_NAME = "DAENERYS";
  private static final String EXPECTED_LAST_NAME = "TARGARYEN";
  private static final String EXPECTED_MIDDLE_NAME = "MIDDLE";
  private static final long EXPECTED_EDIPI = 1234567890;

  private static final String[] SUPPORTED_FULL_SUBJECT_DN =
      {"CN=TARGARYEN.DAENERYS.MIDDLE.1234567890,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US",
          "CN=TARGARYEN.DAENERYS.MIDDLE.1234567890,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US",
          "CN=TARGARYEN.DAENERYS.MIDDLE.1234567890"};

  private static final String[] SUPPORTED_NO_MIDDLE_SUBJECT_DN =
      {"CN=TARGARYEN.DAENERYS.1234567890,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US",
          "CN=TARGARYEN.DAENERYS.1234567890,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US",
          "CN=TARGARYEN.DAENERYS.1234567890", "CN=TARGARYEN.DAENERYS.1234567890"};

  private static final String SUPPORTED_FULL_DN_CN = "TARGARYEN.DAENERYS.MIDDLE.1234567890";
  private static final String SUPPORTED_NO_MIDDLE_DN_CN = "TARGARYEN.DAENERYS.1234567890";

  private static final String[] INVALID_SUBJECT_DN =
      {"TARGARYEN", "TARGARYEN.DAENERYS.123", "", "CN="};

  // @formatter:off
  private static final String BASE64_CERT = "...";
  // @formatter:on

  @Test
  public void extractCnFromSubjectDn_validSubjectDn_shouldExtractCorrectCn() {
    for (String subject : SUPPORTED_FULL_SUBJECT_DN) {
      String cn = X509Extraction.extractCnFromSubjectDn(subject);
      assertEquals(EXPECTED_FULL_CN, cn, "incorrect cn extracted input(" + subject + ")");
    }
    for (String subject : SUPPORTED_NO_MIDDLE_SUBJECT_DN) {
      String cn = X509Extraction.extractCnFromSubjectDn(subject);
      assertEquals(EXPECTED_NO_MIDDLE_CN, cn, "incorrect cn extracted input(" + subject + ")");
    }
  }

  @Test
  public void extractCnFromSubjectDn_invalidSubjectDn_shouldReturnNull() {
    for (String subject : INVALID_SUBJECT_DN) {
      String cn = X509Extraction.extractCnFromSubjectDn(subject);
      assertNull(cn,
          "extracted identity from invalid subject input(" + subject + ") --> output(" + cn + ")");
    }
    assertThrows(NullPointerException.class, () -> X509Extraction.extractCnFromSubjectDn(null));
  }

  @Test
  public void extractCommonNameFromCn_validCn_shouldProduceCorrectCommonName()
      throws X509Exception {
    CommonName cnObj = X509Extraction.extractCommonNameFromCn(SUPPORTED_FULL_DN_CN);
    assertEquals(EXPECTED_FIRST_NAME, cnObj.getFirstName());
    assertEquals(EXPECTED_LAST_NAME, cnObj.getLastName());
    assertEquals(EXPECTED_MIDDLE_NAME, cnObj.getMiddleName());
    assertEquals(EXPECTED_EDIPI, cnObj.getEdipi());

    cnObj = X509Extraction.extractCommonNameFromCn(SUPPORTED_NO_MIDDLE_DN_CN);
    assertEquals(EXPECTED_FIRST_NAME, cnObj.getFirstName());
    assertEquals(EXPECTED_LAST_NAME, cnObj.getLastName());
    assertNull(cnObj.getMiddleName());
    assertEquals(EXPECTED_EDIPI, cnObj.getEdipi());
  }

  @Test
  public void extractCommonNameFromCn_invalidCn_shouldProduceException() {
    assertThrows(NullPointerException.class, ()
        -> X509Extraction.extractCommonNameFromCn(null));

    assertThrows(X509Exception.class, () ->
        X509Extraction.extractCommonNameFromCn("hello.world"));

    assertThrows(X509Exception.class, () ->
        X509Extraction.extractCommonNameFromCn("hello.world.middle.123xxx456"));

    assertThrows(X509Exception.class, () ->
        X509Extraction.extractCommonNameFromCn("hello.world.middle.unexpectedPart.11111"));
  }

  @Test
  public void buildCertChainFromBase64Encoding() throws X509Exception {
    X509Certificate[] certChain = X509Extraction.buildCertChainFromBase64Encoding(BASE64_CERT);
    String cn = X509Extraction.extractCnFromSubjectDn(X509Extraction
        .extractPrimarySubjectDnFromCert(X509Extraction.extractPrimaryCertFromChain(certChain)));

    assertEquals("awesome", cn);
  }

  @Test
  public void extractPrimaryEmailFromCert() throws X509Exception {
    X509Certificate[] certChain = X509Extraction.buildCertChainFromBase64Encoding(BASE64_CERT);
    InternetAddress email = X509Extraction
        .extractPrimaryEmailFromCert(X509Extraction.extractPrimaryCertFromChain(certChain));

    assertEquals("person@example.com", email.getAddress());
  }

}
