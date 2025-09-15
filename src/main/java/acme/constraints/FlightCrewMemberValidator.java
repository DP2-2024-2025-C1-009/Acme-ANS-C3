
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.features.flightCrewMember.flightAssignment.CrewMemberFlightAssignmentRepository;
import acme.realms.flightCrewMembers.FlightCrewMember;

public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	protected void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember member, final ConstraintValidatorContext context) {
		assert context != null;

		if (member == null || member.getUserAccount() == null || member.getEmployeeCode() == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		final String code = member.getEmployeeCode().trim().toUpperCase();

		final String name = member.getUserAccount().getIdentity().getName().trim();
		final String surname = member.getUserAccount().getIdentity().getSurname().trim().replaceAll("\\s+", " ");
		final String[] surnameParts = surname.trim().split(" +");

		final String initials2 = name.substring(0, 1).toUpperCase() + surnameParts[0].substring(0, 1).toUpperCase();
		String initials3 = null;

		if (surnameParts.length >= 2)
			initials3 = initials2 + surnameParts[1].substring(0, 1).toUpperCase();

		boolean codeStartsCorrectly;
		if (surnameParts.length == 1)
			codeStartsCorrectly = code.startsWith(initials2);
		else
			codeStartsCorrectly = code.startsWith(initials2) || code.startsWith(initials3);

		final boolean matchesPattern = code.matches("^([A-Z]{2,3})(\\d{6})$");

		final FlightCrewMember sameCode = this.repository.findMemberSameCode(code);
		final boolean unique = sameCode == null || sameCode.equals(member);

		super.state(context, codeStartsCorrectly, "employeeCode", "validation.CrewMember.codePattern");
		super.state(context, matchesPattern, "employeeCode", "validation.CrewMember.codePattern");
		super.state(context, unique, "employeeCode", "validation.CrewMember.codeNotUnique");

		return !super.hasErrors(context);
	}

}
