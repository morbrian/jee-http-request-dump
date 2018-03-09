package morbrian.jeesandbox.requestdump.filter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class X509IdentityTest {

  private static final String[] SUPPORTED_SUBJECTS =
      {"CN=MORIARTY.BRIAN.CHRISTOPHER.1271726445,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US,",
          "CN=MORIARTY.BRIAN.CHRISTOPHER.1271726445,OU=CONTRACTOR,OU=PKI,OU=DoD,O=U.S. Government,C=US",
          "CN=MORIARTY.BRIAN.CHRISTOPHER.1271726445,", "CN=MORIARTY.BRIAN.CHRISTOPHER.1271726445",
          "MORIARTY.BRIAN.CHRISTOPHER.1271726445,", "MORIARTY.BRIAN.CHRISTOPHER.1271726445",
          "MORIARTY.BRIAN.1271726445"};

  private static final String[] INVALID_SUBJECTS = {"MORIARTY", "MORIARTY.123", "", "CN=", null};

  @Test
  public void shouldDetectValidSubjects() {
    for (String subject : SUPPORTED_SUBJECTS) {
      X509Identity identity = new X509Identity(subject);
      assertTrue(identity.hasCert(), "should detect valid identity: " + subject);
    }
  }

  @Test
  public void shouldDetectInvalidSubjects() {
    for (String subject : INVALID_SUBJECTS) {
      X509Identity identity = new X509Identity(subject);
      assertFalse(identity.hasCert(), "should detect invalid identity: " + subject);
    }
  }
}
