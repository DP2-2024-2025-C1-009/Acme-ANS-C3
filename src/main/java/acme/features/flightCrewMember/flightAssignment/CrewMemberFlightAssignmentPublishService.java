
package acme.features.flightCrewMember.flightAssignment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberStatus;

@GuiService
public class CrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int id = super.getRequest().getData("id", Integer.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);

		boolean authorised = false;

		if (assignment != null && assignment.getDraftMode()) {
			int principalId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean isOwner = assignment.getCrewMember().getId() == principalId;

			Object legData = super.getRequest().getData().get("leg");
			if (legData instanceof String legKey) {
				legKey = legKey.trim();

				if (legKey.equals("0"))
					authorised = isOwner;
				else if (legKey.matches("\\d+")) {
					int legId = Integer.parseInt(legKey);
					Leg leg = this.repository.findLegById(legId);
					boolean legValid = leg != null && !leg.isDraftMode() && this.repository.findAllLegs().contains(leg);
					authorised = isOwner && legValid;
				}
			}

		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {

		super.bindObject(assignment, "duty", "status", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		if (assignment.getLeg() != null) {
			boolean isPast = assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(!isPast, "leg", "acme.validation.flightAssignment.leg.moment");

			Date start = assignment.getLeg().getScheduledDeparture();
			Date end = assignment.getLeg().getScheduledArrival();
			boolean overlaps = this.repository.isOverlappingAssignmentExcludingSelf(assignment.getCrewMember(), start, end, assignment.getId());
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");

			boolean isLegDraft = assignment.getLeg().isDraftMode();
			super.state(!isLegDraft, "leg", "acme.validation.flightAssignment.legNotPublished");
		}

		if (assignment.getCrewMember() != null) {
			boolean isAvailable = assignment.getCrewMember().getFlightCrewMemberStatus().equals(FlightCrewMemberStatus.AVAILABLE);
			super.state(isAvailable, "crewMember", "acme.validation.flightAssignment.crewMember.available");
		}

		Leg selectedLeg = assignment.getLeg();

		if (selectedLeg != null) {
			boolean pilotAssigned = this.repository.hasDutyAssignedExcludingSelf(selectedLeg, Duty.PILOT, assignment.getId());
			boolean coPilotAssigned = this.repository.hasDutyAssignedExcludingSelf(selectedLeg, Duty.CO_PILOT, assignment.getId());

			if (assignment.getDuty() == Duty.PILOT)
				super.state(!pilotAssigned, "duty", "acme.validation.flightAssignment.crewMember.onlyOnePilot");

			if (assignment.getDuty() == Duty.CO_PILOT)
				super.state(!coPilotAssigned, "duty", "acme.validation.flightAssignment.crewMember.onlyOneCoPilot");
		}
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setDraftMode(false);
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		int principalId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(principalId);

		List<Leg> legs = this.repository.findSelectableLegs(MomentHelper.getCurrentMoment());
		if (legs == null)
			legs = List.of();

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");
		data.put("readonly", false);
		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("legChoices", legChoices);
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());

		super.getResponse().addData(data);
	}

}
