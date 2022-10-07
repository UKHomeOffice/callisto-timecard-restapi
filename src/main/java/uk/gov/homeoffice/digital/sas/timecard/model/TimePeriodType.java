package uk.gov.homeoffice.digital.sas.timecard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Resource(path = "time-period-types")
@Entity(name = "time_period_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class TimePeriodType extends BaseEntity {

    @NotEmpty
    private String name;
}
