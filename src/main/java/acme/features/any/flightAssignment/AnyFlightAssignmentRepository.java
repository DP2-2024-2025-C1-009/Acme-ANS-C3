
package acme.features.any.flightAssignment;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface AnyFlightAssignmentRepository extends AbstractRepository {

	@Query("select l from Leg l")
	List<Leg> findAllLegs();

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.draftMode = false")
	List<FlightAssignment> findPublished();

	@Query("select f from FlightAssignment f where f.id = :id and f.draftMode = false")
	FlightAssignment findPublishedById(int id);

	@Query("select m from FlightCrewMember m where m.flightCrewMemberStatus = acme.realms.flightCrewMembers.FlightCrewMemberStatus.AVAILABLE")
	Collection<FlightCrewMember> findAvailableMembers();
}
