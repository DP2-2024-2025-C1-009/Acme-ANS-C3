
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.realms.flightCrewMembers.FlightCrewMember;

public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Override
	protected void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember member, final ConstraintValidatorContext context) {

		boolean result = false;

		if (member == null || member.getEmployeeCode() == null) {
			result = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{acme.validation.flightCrewMember.notNull}").addConstraintViolation();
		} else {

			DefaultUserIdentity identity = member.getIdentity();
			String identifierNumber = member.getEmployeeCode();
			String name = identity.getName();
			String surname = identity.getSurname();

			char identifierFirstChar = Character.toUpperCase(identifierNumber.charAt(0));
			char identifierSecondChar = Character.toUpperCase(identifierNumber.charAt(1));
			char nameFirstChar = Character.toUpperCase(name.charAt(0));
			char surnameFirstChar = Character.toUpperCase(surname.charAt(0));
			if (identifierFirstChar == nameFirstChar && identifierSecondChar == surnameFirstChar)
				result = true;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{acme.validation.flightCrewMember.notCorrect}").addConstraintViolation();
		}
		return result;
	}

}
