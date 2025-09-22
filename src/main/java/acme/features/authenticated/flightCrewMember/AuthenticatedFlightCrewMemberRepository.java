
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select f from FlightCrewMember f where f.userAccount.id = :id")
	FlightCrewMember findFlightCrewMemberByUserAccountId(int id);

	@Query("select u from UserAccount u where u.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a FROM Airline a")
	Collection<Airline> finAllAirlines();

}
