
package acme.features.administrator.trackingLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.agents.Claim;
import acme.entities.agents.TrackingLog;

@Repository
public interface AdministratorTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.trackingSteps.id = :masterId order by t.id asc")
	Collection<TrackingLog> findAllTrackingLogsByMasterId(int masterId);

	@Query("select t from TrackingLog t where t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

}
