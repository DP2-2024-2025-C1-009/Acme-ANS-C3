
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.components.ServiceRepository;
import acme.entities.service.Service;

public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Autowired
	private ServiceRepository repository;


	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {

			Service exists = this.repository.findServiceByPromotionCode(service.getPromotionCode());
			boolean unique = exists == null || exists.equals(service);

			super.state(context, unique, "promotionCode", "acme.validation.service.duplicated-promotionCode");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
