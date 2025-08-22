
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
		boolean valid = true;

		if (member == null)
			return true;

		String code = member.getEmployeeCode();
		boolean codeOk = code != null && !code.isBlank() && code.length() >= 2;
		super.state(context, codeOk, "employeeCode", "{acme.validation.flightCrewMember.code.requiredOrTooShort}");
		valid &= codeOk;

		DefaultUserIdentity identity = member.getIdentity();
		boolean hasIdentity = identity != null;
		super.state(context, hasIdentity, "userAccount", "{acme.validation.flightCrewMember.identity.required}");
		valid &= hasIdentity;

		String name = hasIdentity ? identity.getName() : null;
		String surname = hasIdentity ? identity.getSurname() : null;

		boolean nameOk = name != null && !name.isBlank();
		boolean surnameOk = surname != null && !surname.isBlank();

		super.state(context, nameOk, "userAccount.identity.name", "{acme.validation.flightCrewMember.name.required}");
		super.state(context, surnameOk, "userAccount.identity.surname", "{acme.validation.flightCrewMember.surname.required}");
		valid &= nameOk && surnameOk;

		if (valid) {
			char c0 = Character.toUpperCase(code.charAt(0));
			char c1 = Character.toUpperCase(code.charAt(1));
			char n0 = Character.toUpperCase(name.trim().charAt(0));
			char s0 = Character.toUpperCase(surname.trim().charAt(0));

			boolean initialsMatch = c0 == n0 && c1 == s0;
			super.state(context, initialsMatch, "employeeCode", "{acme.validation.flightCrewMember.initials.mismatch}");
			valid &= initialsMatch;
		}

		return valid;
	}
}
