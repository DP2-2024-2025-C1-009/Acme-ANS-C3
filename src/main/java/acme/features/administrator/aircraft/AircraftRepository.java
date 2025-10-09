
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airline.Airline;

@Repository
public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(@Param("id") int id);

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("select a from Aircraft a where a.numberRegistration = :numberRegistration")
	Aircraft findAircraftByNumberRegistration(String numberRegistration);

	@Query("select ai from Airline ai")
	Collection<Airline> findAllAirlines();

	@Query("select ai from Airline ai where ai.id =:airlineId")
	Airline findAirlineById(Integer airlineId);

}
