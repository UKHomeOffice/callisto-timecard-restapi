package uk.gov.homeoffice.digital.sas.timecard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Resource(path = "time-entry")
@Entity(name = "time_entry")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class TimeEntry extends BaseEntity {

    private int version;

    @NotNull
    @Column(name = "owner_id")
    private int ownerId;

    @Type(type="uuid-char")
    @Column(name = "time_period_type_id")
    private UUID timePeriodTypeId;

    @ManyToOne
    @JoinColumn(name="time_period_type_id", referencedColumnName = "id", unique = true, nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private TimePeriodType timePeriodType;

    @Column(name = "shift_type")
    private String shiftType;

    @NotNull
    @Column(name = "actual_start_time")
    private Date actualStartTime;

    @Column(name = "actual_end_time")
    private Date actualEndTime;

    @Column(name = "planned_start_time")
    private Date plannedStartTime;

    @Column(name = "planned_end_time")
    private Date plannedEndTime;

    @NotNull
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted")
    private boolean deleted;

}