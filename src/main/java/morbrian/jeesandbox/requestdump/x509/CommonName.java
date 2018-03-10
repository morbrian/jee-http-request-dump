package morbrian.jeesandbox.requestdump.x509;

public class CommonName {

  private final String firstName;
  private final String lastName;
  private final String middleName;
  private final long edipi;

  /**
   * Construct common name object.
   *
   * @param edipi 10 digit edipi identifier
   * @param lastName last name
   * @param firstName first name
   * @param middle middle name or null if unavailable
   */
  public CommonName(long edipi, String lastName, String firstName, String middle) {
    this.edipi = edipi;
    this.lastName = lastName;
    this.firstName = firstName;
    this.middleName = middle;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public long getEdipi() {
    return edipi;
  }
}
