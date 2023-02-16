package uk.gov.homeoffice.digital.sas.timecard.repositories;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {

  @Query("SELECT t FROM time_entry t "
      + "WHERE t.ownerId = :ownerId "
      + "AND (cast(:id as string) IS NULL OR t.id <> :id) "
      + "AND (:tenantId IS NULL OR t.tenantId = :tenantId)"
      + "AND ((:newActualStartTime = t.actualStartTime) " // start times are the same
      + "OR ((t.actualStartTime <= :newActualStartTime) "
      + "AND (:newActualStartTime < t.actualEndTime)) " // new start time is in existing entry
      + "OR ((:newActualStartTime <= t.actualStartTime) "
      + "AND (t.actualStartTime < :newActualEndTime)))") // existing start time is in new time entry

  List<TimeEntry> findAllClashingTimeEntries(@Param("ownerId") UUID ownerId,
                                             @Param("id") UUID id,
                                             @Param("tenantId") UUID tenantId,
                                             @Param("newActualStartTime") Date actualStartTime,
                                             @Param("newActualEndTime") Date actualEndTime);
}

