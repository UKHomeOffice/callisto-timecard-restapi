package uk.gov.homeoffice.digital.sas.timecard.kafka.exceptions;

//@Target(ElementType.METHOD)
//@Retention(RUNTIME)
//@Documented
public class KafkaInterruptedException extends InterruptedException {
  public KafkaInterruptedException(String message) {
    super(message);
  }
}
