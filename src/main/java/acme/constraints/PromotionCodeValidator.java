
package acme.constraints;

import java.time.Year;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class PromotionCodeValidator extends AbstractValidator<ValidPromotionCode, String> {

	private static final String PATTERN = "^[A-Z]{4}-[0-9]{2}$";


	@Override
	public boolean isValid(final String code, final ConstraintValidatorContext context) {
		if (code == null || code.isBlank())
			return true;

		if (!code.matches(PromotionCodeValidator.PATTERN))
			return false;

		String promotionCodeYear = code.substring(code.length() - 2);
		int currentYear = Year.now().getValue() % 100;
		return promotionCodeYear.equals(String.format("%02d", currentYear));

	}

}
