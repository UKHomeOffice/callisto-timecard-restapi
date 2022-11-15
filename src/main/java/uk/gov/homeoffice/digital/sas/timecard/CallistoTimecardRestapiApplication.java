package uk.gov.homeoffice.digital.sas.timecard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CallistoTimecardRestapiApplication {
  public static void main(String[] args) {
    SpringApplication.run(CallistoTimecardRestapiApplication.class, args);
    System.out.println("To jest wersja v3");
  }
}
