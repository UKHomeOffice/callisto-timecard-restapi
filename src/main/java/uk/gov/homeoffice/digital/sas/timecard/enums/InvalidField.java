package uk.gov.homeoffice.digital.sas.timecard.enums;

public enum InvalidField {
  START_TIME("startTime"),
  END_TIME("endTime"),
  START_AND_END_TIME("startAndEndTime");
  private final String stringValue;

  InvalidField(final String s) {
    stringValue = s;
  }

  @Override
  public String toString() {
    return stringValue;
  }
}