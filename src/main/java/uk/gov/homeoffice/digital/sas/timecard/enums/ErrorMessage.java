package uk.gov.homeoffice.digital.sas.timecard.enums;

public enum ErrorMessage {

  END_TIME_BEFORE_START_TIME("End time must be after start time"),
  TIME_PERIOD_CLASH("Time periods must not overlap with another time period");

  private final String stringValue;

  ErrorMessage(final String s) {
    stringValue = s;
  }

  @Override
  public String toString() {
    return stringValue;
  }
}
