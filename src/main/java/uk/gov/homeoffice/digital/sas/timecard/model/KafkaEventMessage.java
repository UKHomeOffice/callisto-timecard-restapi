package uk.gov.homeoffice.digital.sas.timecard.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class KafkaEventMessage {

  @NotNull
  private TimeEntry resource;

  @NotNull
  private KafkaAction action;

}
