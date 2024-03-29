package uk.gov.homeoffice.digital.sas.timecard.kafka;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;

@Getter
public class KafkaEventMessage<T> {

  private final String schema;

  @NotNull
  private final T resource;

  @NotNull
  private final KafkaAction action;

  public KafkaEventMessage(String schemaVersion, Class<T> resourceType, T resource,
                           KafkaAction action) {
    this.schema = resourceType.getName() + ", " + schemaVersion;
    this.resource = resource;
    this.action = action;
  }
}
