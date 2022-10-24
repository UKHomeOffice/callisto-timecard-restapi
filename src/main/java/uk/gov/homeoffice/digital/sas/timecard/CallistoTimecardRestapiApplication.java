package uk.gov.homeoffice.digital.sas.timecard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CallistoTimecardRestapiApplication {
  public static void main(String[] args) {
    dummyMethod();
    SpringApplication.run(CallistoTimecardRestapiApplication.class, args);
  }

  private static void dummyMethod() {
    System.out.println("dummyMethod");
  }
}
