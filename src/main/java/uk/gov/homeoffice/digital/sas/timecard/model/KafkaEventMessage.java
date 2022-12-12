package uk.gov.homeoffice.digital.sas.timecard.model;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;

@Getter
@Setter
public class KafkaEventMessage<T> {
  public KafkaEventMessage(Class<T> resourceType, T resource, KafkaAction action) {
    this.schema = resourceType.getPackage().getImplementationVersion();
    this.resource = resource;
    this.action = action;
  }

  private String schema;

  @NotNull
  private T resource;

  @NotNull
  private KafkaAction action;

}
