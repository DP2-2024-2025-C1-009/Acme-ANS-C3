
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.flightAssignment.FlightAssignment;
import acme.features.flightCrewMember.flightAssignment.CrewMemberFlightAssignmentRepository;

@Validator
public class FlightAssignmentValidator extends AbstractValidator<ValidFlightAssignment, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void initialise(final ValidFlightAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightAssignment flightAssignment, final ConstraintValidatorContext context) {
		assert context != null;

		if (flightAssignment.getLeg() != null) {
			if (flightAssignment.getDuty() != null) {
				boolean isDutyAlreadyAssigned = this.repository.hasDutyAssignedExcludingSelf(flightAssignment.getLeg(), flightAssignment.getDuty(), flightAssignment.getId()) && flightAssignment.getDraftMode() == false;
				super.state(context, !isDutyAlreadyAssigned, "duty", "acme.validation.flightAssignment.dutyIsAssigned");
			}
			boolean overlaps = this.repository.isOverlappingAssignmentExcludingSelf(flightAssignment.getCrewMember(), flightAssignment.getLeg().getScheduledDeparture(), flightAssignment.getLeg().getScheduledArrival(), flightAssignment.getId())
				&& flightAssignment.getDraftMode() == false;
			super.state(context, !overlaps, "leg", "acme.validation.flightAssignment.crewMember.overlaps");
			boolean isLegDraft = flightAssignment.getLeg().isDraftMode();
			super.state(context, !isLegDraft, "leg", "acme.validation.flightAssignment.legIsNotPublished");
		}

		return !super.hasErrors(context);
	}

}
