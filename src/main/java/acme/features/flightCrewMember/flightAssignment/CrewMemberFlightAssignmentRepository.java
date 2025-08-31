
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface CrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.crewMember.id = :crewId and f.leg.scheduledArrival < :now")
	Collection<FlightAssignment> findCompletedAssignmentByMemberId(int crewId, Date now);

	@Query("select f from FlightAssignment f where f.crewMember.id = :crewId and f.leg.scheduledArrival >= :now")
	Collection<FlightAssignment> findPlannedAssignmentsByMemberId(int crewId, Date now);

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findAssignmentById(int id);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("select distinct f.leg from FlightAssignment f where f.crewMember.id = :memberId")
	List<Leg> findLegsByMemberId(int memberId);

	@Query("select f from FlightAssignment f")
	Collection<FlightAssignment> findAllFlightAssignments();

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.aircraft.airline.id = :airlineId")
	Collection<Leg> findAllLegsByAirlineId(int airlineId);

	@Query("select f from FlightAssignment f where f.leg.id = :legId")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int legId);

	@Query("SELECT COUNT(f) > 0 FROM FlightAssignment f WHERE f.crewMember = :crewMember AND f.leg.scheduledDeparture < :end AND f.leg.scheduledArrival > :start AND f.draftMode = false")
	Boolean isOverlappingAssignment(FlightCrewMember crewMember, java.util.Date start, java.util.Date end);

	@Query("SELECT COUNT(f) > 0 FROM FlightAssignment f WHERE f.crewMember = :crewMember AND f.id <> :currentId AND f.leg.scheduledDeparture < :end AND f.leg.scheduledArrival > :start AND f.draftMode = false")
	boolean isOverlappingAssignmentExcludingSelf(FlightCrewMember crewMember, Date start, Date end, int currentId);

	@Query("SELECT COUNT(f) > 0 FROM FlightAssignment f WHERE f.leg = :leg AND f.duty = :duty AND f.id <> :currentId AND f.draftMode = false")
	boolean hasDutyAssignedExcludingSelf(Leg leg, Duty duty, int currentId);

	@Query("SELECT COUNT(f) FROM FlightAssignment f WHERE f.leg = :leg AND f.duty = :duty")
	long countByLegAndDuty(Leg leg, Duty duty);

	@Query("SELECT COUNT(f) > 0 FROM FlightAssignment f WHERE f.crewMember.id = :crewMemberId AND f.moment = :moment AND f.draftMode = false")
	Boolean hasFlightCrewMemberLegAssociated(int crewMemberId, Date moment);

	@Query("SELECT COUNT(f) > 0 FROM FlightAssignment f WHERE f.crewMember = :crewMember AND f.leg = :leg")
	boolean isAlreadyAssignedToLeg(FlightCrewMember crewMember, Leg leg);

	@Query("SELECT l FROM Leg lWHERE l.aircraft.airline.id = :airlineId AND l.draftMode = false")
	Collection<Leg> findPublishedLegsByAirlineId(int airlineId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false")
	Collection<Leg> findPublishedLegs();

	@Query("SELECT COUNT(f) FROM FlightAssignment f WHERE f.crewMember = :crewMember")
	int countByCrewMember(FlightCrewMember crewMember);

	@Query("SELECT a FROM ActivityLog a WHERE a.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findAllActivityLogs(int flightAssignmentId);

	@Query("select m from FlightCrewMember m where m.id = :memberId")
	FlightCrewMember findMemberById(int memberId);

	@Query("SELECT m FROM FlightCrewMember m WHERE m.employeeCode= :employeeCode")
	FlightCrewMember findMemberSameCode(String employeeCode);

	@Query("select m from FlightCrewMember m")
	Collection<FlightCrewMember> findAllCrewMembers();

	@Query("select m from FlightCrewMember m where m.id = :memberId")
	FlightCrewMember findCrewMemberById(int memberId);

}
