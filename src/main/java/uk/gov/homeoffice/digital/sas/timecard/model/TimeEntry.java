package uk.gov.homeoffice.digital.sas.timecard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.homeoffice.digital.sas.jparest.annotation.Resource;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Resource(path = "time-entries")
@Entity(name = "time_entry")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@Getter
@Setter
public class TimeEntry extends BaseEntity {

    @Column(name = "owner_id")
    @Min( value = 1, message = "Owner id's value must be greater than or equal to {value}")
    private int ownerId;

    @Type(type="uuid-char")
    @Column(name = "time_period_type_id")
    private UUID timePeriodTypeId;

    @ManyToOne
    @JoinColumn(name="time_period_type_id", referencedColumnName = "id", unique = true, nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private TimePeriodType timePeriodType;

    @Column(name = "shift_type")
    @Size(max = 50, message = "Shift type must be less than or equal to {max}")
    private String shiftType;

    @NotNull(message = "Actual start time should not be empty")
    @Column(name = "actual_start_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant actualStartTime;

    @Column(name = "actual_end_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant actualEndTime;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime updatedAt;

}