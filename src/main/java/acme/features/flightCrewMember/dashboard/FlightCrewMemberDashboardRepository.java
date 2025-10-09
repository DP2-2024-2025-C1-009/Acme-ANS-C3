
package acme.features.flightCrewMember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	@Query("SELECT l.arrivalAirport.airportName FROM FlightAssignment f JOIN f.leg l WHERE f.crewMember.id = :crewMemberId ORDER BY f.lastUpdate DESC")
	List<String> findLastDestinations(int crewMemberId);

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 0 AND 3 THEN l.id END) FROM ActivityLog al JOIN al.activityLogAssignment.leg l")
	Integer findLegsCountBySeverityLevelsLow();

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 4 AND 7 THEN l.id END) FROM ActivityLog al JOIN al.activityLogAssignment.leg l")
	Integer findLegsCountBySeverityLevelsMedium();

	@Query("SELECT COUNT(DISTINCT CASE WHEN al.severityLevel BETWEEN 8 AND 10 THEN l.id END) FROM ActivityLog al JOIN al.activityLogAssignment.leg l")
	Integer findLegsCountBySeverityLevelsHigh();

	@Query("SELECT DISTINCT fa.crewMember FROM FlightAssignment fa WHERE fa.leg.id = (SELECT MAX(fa2.leg.id) FROM FlightAssignment fa2 WHERE fa2.crewMember.id = :crewMemberId)")
	List<FlightCrewMember> findCrewNamesInLastLeg(int crewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.crewMember.id =:crewMemberId and fa.status =:status")
	List<FlightAssignment> findFlightAssignmentsByCrewMember(int crewMemberId, AssignmentStatus status);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.lastUpdate BETWEEN :startDate AND :endDate AND fa.crewMember.id = :crewMemberId GROUP BY FUNCTION('DATE', fa.lastUpdate)")
	List<Long> getDailyAssignmentCounts(Date startDate, Date endDate, int crewMemberId);

}
