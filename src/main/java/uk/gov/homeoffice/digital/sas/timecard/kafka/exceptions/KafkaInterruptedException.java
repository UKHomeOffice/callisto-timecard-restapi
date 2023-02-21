package uk.gov.homeoffice.digital.sas.timecard.kafka.exceptions;

public class KafkaInterruptedException extends InterruptedException {
  public KafkaInterruptedException(String message) {
    super(message);
  }
}
