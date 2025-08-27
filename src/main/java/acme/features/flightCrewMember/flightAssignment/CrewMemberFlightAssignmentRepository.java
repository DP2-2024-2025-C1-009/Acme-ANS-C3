
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface CrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.crewMember.id = :crewId and f.leg.scheduledArrival <= :now")
	Collection<FlightAssignment> findCompletedAssignmentByMemberId(int crewId, Date now);

	@Query("select f from FlightAssignment f where f.crewMember.id = :crewId and f.leg.scheduledArrival > :now")
	Collection<FlightAssignment> findPlannedAssignmentsByMemberId(int crewId, Date now);

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findAssignmentById(int id);

	@Query("select m from FlightCrewMember m where m.id = :id")
	FlightCrewMember findMemberById(int id);

	@Query("select l from Leg l")
	List<Leg> findAllLegs();

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT DISTINCT f.leg FROM FlightAssignment f WHERE f.crewMember.id = :memberId and f.id != :fId ")
	List<Leg> findLegsByMemberId(int memberId, int fId);

	@Query("select log from ActivityLog log where log.activityLogAssignment.id = :assignmentId")
	List<ActivityLog> findRelatedLogs(int assignmentId);

	@Query("select f from FlightAssignment f where f.duty =  acme.entities.flightAssignment.Duty.PILOT and f.leg.id = :idLeg and f.draftMode = false")
	List<FlightAssignment> findPilot(int idLeg);

	@Query("select f from FlightAssignment f where f.duty =  acme.entities.flightAssignment.Duty.CO_PILOT and f.leg.id = :idLeg and f.draftMode = false")
	List<FlightAssignment> findCopilot(int idLeg);

	@Query("select l from Leg l where l.draftMode = false and l.scheduledDeparture > :now and l.airline.id = :airlineId order by l.scheduledDeparture asc")
	List<Leg> findSelectableLegsForMember(int memberId, int airlineId, Date now);

}
