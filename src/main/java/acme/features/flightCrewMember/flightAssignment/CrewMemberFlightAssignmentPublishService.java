
package acme.features.flightCrewMember.flightAssignment;

import java.util.Arrays;
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
import acme.entities.legs.LegStatus;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberStatus;

@GuiService
public class CrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean authorised;
		boolean validLeg = true;
		boolean validDuty = true;
		boolean validStatus = true;
		String method = super.getRequest().getMethod();
		boolean exists = false;
		boolean isOwner = false;
		boolean isPublished = false;
		boolean falsePublish = false;

		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			falsePublish = true;
			Integer fId = super.getRequest().getData("id", Integer.class);
			if (fId != null) {
				FlightCrewMember member = this.repository.findMemberById(id);
				FlightAssignment assignment = this.repository.findAssignmentById(fId);
				exists = assignment != null;
				if (exists) {
					isOwner = assignment.getCrewMember() == member;
					if (method.equals("GET"))
						isPublished = !assignment.isDraftMode();
				}
			}
			if (method.equals("POST")) {
				Integer legId = super.getRequest().getData("leg", Integer.class);

				if (legId == null)
					validLeg = false;
				else {
					Leg leg = this.repository.findLegById(legId);
					if (leg == null && legId != 0)
						validLeg = false;
				}

				String duty = super.getRequest().getData("duty", String.class);
				if (duty == null || Arrays.stream(Duty.values()).noneMatch(tc -> tc.name().equals(duty)) && !duty.equals("0"))
					validDuty = false;

				String status = super.getRequest().getData("status", String.class);
				if (status == null || Arrays.stream(FlightCrewMemberStatus.values()).noneMatch(cs -> cs.name().equals(status)) && !status.equals("0"))
					validStatus = false;

			}
		}
		authorised = isOwner && validLeg && validDuty && validStatus && !isPublished && falsePublish;
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
		int legId;
		Leg leg;
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		super.bindObject(assignment, "duty", "status", "remarks");
		assignment.setLeg(leg);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCrewMember(member);
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		// Comprobación de leg no null
		boolean isLegNull;
		isLegNull = assignment.getLeg() != null;
		if (isLegNull) {

			// Comprobación de leg no pasada
			boolean legNotPast = assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(!legNotPast, "legRelated", "acme.validation.legNotPast.message");

			// Comprobación de leg no completada
			boolean legNotCompleted = assignment.getLeg().getStatus().equals(LegStatus.ON_TIME) || assignment.getLeg().getStatus().equals(LegStatus.DELAYED);
			super.state(legNotCompleted, "leg", "acme.validation.legNotCompleted.message");

			// Comprobación de leg no publicada
			boolean legNotPublished = !assignment.getLeg().isDraftMode();
			super.state(legNotPublished, "leg", "acme.validation.legNotPublished.message");

			// Comprobación de que el FCM esté AVAILABLE
			boolean memberAvailable = assignment.getCrewMember().getFlightCrewMemberStatus().equals(FlightCrewMemberStatus.AVAILABLE);
			super.state(memberAvailable, "*", "acme.validation.memberAvailableCreate.message");
		}

		// Comprobación de 1 piloto y un copiloto
		boolean pilotExceptionPassed = true;
		boolean coPilotExceptionPassed = true;
		if (assignment.getDuty() != null) {
			Integer pilot = this.repository.findPilot(assignment.getLeg().getId()).size();
			Integer copilot = this.repository.findCopilot(assignment.getLeg().getId()).size();

			Duty duty = assignment.getDuty();
			if (duty.equals(Duty.PILOT) && pilot >= 1 && !assignment.getStatus().equals(AssignmentStatus.CANCELLED))
				pilotExceptionPassed = false;
			else if (duty.equals(Duty.CO_PILOT) && copilot >= 1 && !assignment.getStatus().equals(AssignmentStatus.CANCELLED))
				coPilotExceptionPassed = false;

			super.state(pilotExceptionPassed, "legRelated", "acme.validation.pilotExceptionPassed.message");
			super.state(coPilotExceptionPassed, "legRelated", "acme.validation.copilotExceptionPassed.message");
		}

		boolean legCompatible = true;

		List<Leg> legs = this.repository.findLegsByMemberId(assignment.getCrewMember().getId(), assignment.getId()).stream().toList();
		for (Leg l : legs)
			if (this.legIsCompatible(assignment.getLeg(), l)) {
				legCompatible = false;
				super.state(legCompatible, "leg", "acme.validation.legCompatible.message");
				break;
			}
	}

	private boolean legIsCompatible(final Leg finalLeg, final Leg legToCompare) {
		boolean departureCompatible = MomentHelper.isInRange(finalLeg.getScheduledDeparture(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		boolean arrivalCompatible = MomentHelper.isInRange(finalLeg.getScheduledArrival(), legToCompare.getScheduledDeparture(), legToCompare.getScheduledArrival());
		return departureCompatible && arrivalCompatible;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setDraftMode(false);
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		List<Leg> legs = this.repository.findAllLegs();
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");

		data.put("readonly", false);
		data.put("update", MomentHelper.getCurrentMoment());
		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("legChoices", legChoices);
		data.put("leg", legChoices.getSelected().getKey());
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());
		data.put("confirmation", false);

		super.getResponse().addData(data);
	}

}
