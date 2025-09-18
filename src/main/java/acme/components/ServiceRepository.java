
package acme.components;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.service.Service;

@Repository
public interface ServiceRepository extends AbstractRepository {

	@Query("select s from Service s where s.promotionCode = :promotionCode")
	Service findServiceByPromotionCode(String promotionCode);
}
