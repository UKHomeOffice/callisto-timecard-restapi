package uk.gov.homeoffice.digital.sas.timecard.testutils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class CommonUtils {
  public static Date getAsDate(LocalDateTime dateTime) {
    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
  }
}
