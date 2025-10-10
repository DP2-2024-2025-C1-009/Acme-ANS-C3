
package acme.features.administrator.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.agents.Claim;
import acme.entities.agents.TrackingLog;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.cIsAccepted = true")
	Collection<Claim> findAllPublishedClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select l from Leg l where l.draftMode = false and l.scheduledArrival <= :moment")
	Collection<Leg> findAllPublishedLegsBefore(Date moment);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select a from AssistanceAgent a where a.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("select t from TrackingLog t where t.trackingSteps.id = :id")
	Collection<TrackingLog> findAllTrackingLogsByClaimId(int id);
}
