package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import java.util.Date;
import java.util.UUID;

public record TimeClash(Date startTime, Date endTime, UUID timePeriodTypeId) {
}
