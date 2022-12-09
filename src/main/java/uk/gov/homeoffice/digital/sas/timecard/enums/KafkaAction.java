package uk.gov.homeoffice.digital.sas.timecard.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KafkaAction {
  CREATE("create"),
  UPDATE("update");
  private final String stringValue;

  @Override
  public String toString() {
    return stringValue;
  }
}