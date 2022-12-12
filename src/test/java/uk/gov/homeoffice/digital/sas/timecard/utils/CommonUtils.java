package uk.gov.homeoffice.digital.sas.timecard.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class CommonUtils {
  public static Date getAsDate(LocalDateTime dateTime) {
    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
  }
}
