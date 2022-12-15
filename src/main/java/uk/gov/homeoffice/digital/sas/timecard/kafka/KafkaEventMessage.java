package uk.gov.homeoffice.digital.sas.timecard.kafka;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;

@Getter
public class KafkaEventMessage<T> {

  public KafkaEventMessage(String version, Class<T> resourceType, T resource, KafkaAction action) {
    this.schema = resourceType.getName() + ", " + version;
    this.resource = resource;
    this.action = action;
  }

  private final String schema;

  @NotNull
  private final T resource;

  @NotNull
  private final KafkaAction action;

}
