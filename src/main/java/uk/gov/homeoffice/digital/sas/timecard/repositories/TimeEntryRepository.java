package uk.gov.homeoffice.digital.sas.timecard.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeEntryRepository extends CrudRepository<TimeEntry, UUID> {

    @Query("SELECT t FROM time_entry t WHERE t.ownerId = cast(:ownerId as org.hibernate.type.UUIDCharType) " +
            "AND (:id IS NULL OR t.id <> cast(:id as org.hibernate.type.UUIDCharType)) " +
            "AND ((:newActualStartTime = t.actualStartTime) " + //start times are the same
            "OR ((t.actualStartTime <= :newActualStartTime) AND (:newActualStartTime < t.actualEndTime))" + //new start time is in existing entry
            "OR ((:newActualStartTime <= t.actualStartTime) AND (t.actualStartTime < :newActualEndTime)))") //existing start time is in new time entry

    List<TimeEntry> findAllClashingTimeEntries(@Param("ownerId") String ownerId,
                                          @Param("id") String id,
                                          @Param("newActualStartTime") Date actualStartTime,
                                          @Param("newActualEndTime") Date actualEndTime);
}

