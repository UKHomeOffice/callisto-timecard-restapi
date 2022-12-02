package uk.gov.homeoffice.digital.sas.timecard.enums;

public enum KafkaAction {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete");
  private final String stringValue;

  KafkaAction(final String s) {
    stringValue = s;
  }

  @Override
  public String toString() {
    return stringValue;
  }
}