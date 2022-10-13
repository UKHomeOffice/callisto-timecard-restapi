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

    //@Query("SELECT t FROM time_entry t WHERE t.ownerId = :ownerId AND t.actualStartTime < :newActualEndTime " +
//        "AND :newActualStartTime < t.actualEndTime")
    @Query("SELECT t FROM time_entry t WHERE t.ownerId = :ownerId AND t.actualStartTime <= :newActualStartTime " +
            "AND t.actualEndTime > :newActualStartTime OR t.actualStartTime > :newActualStartTime " +
            "AND t.actualStartTime <> :newActualEndTime")
    List<TimeEntry> findAllClashingTimeEntries(@Param("ownerId") Integer ownerId,
                                          @Param("newActualStartTime") Date actualStartTime,
                                          @Param("newActualEndTime") Date actualEndTime);


}

