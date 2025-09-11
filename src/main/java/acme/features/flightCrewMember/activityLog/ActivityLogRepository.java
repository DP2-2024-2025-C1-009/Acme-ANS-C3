
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select log from ActivityLog log where log.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select log from ActivityLog log WHERE log.activityLogAssignment.id = :id")
	Collection<ActivityLog> findAllActivityLog(int id);

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findAssignmentById(int id);

	@Query("select log from ActivityLog log where log.activityLogAssignment.crewMember.id = :id")
	Collection<ActivityLog> findAllLogsByMemberId(int id);

	@Query("select log from ActivityLog log where log.activityLogAssignment.id = :id")
	Collection<ActivityLog> findLogsByAssignmentId(int id);

}
