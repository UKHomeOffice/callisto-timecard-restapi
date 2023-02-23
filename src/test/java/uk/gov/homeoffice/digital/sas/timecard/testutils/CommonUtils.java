package uk.gov.homeoffice.digital.sas.timecard.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

public final class CommonUtils {
  public static Date getAsDate(LocalDateTime dateTime) {
    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
  }

  public static String generateMessageKey(TimeEntry timeEntry) {
    return timeEntry.getTenantId()  + ":" + timeEntry.getOwnerId();
  }

  public static  String objectAsJsonString(final Object obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(obj);
  }
}
