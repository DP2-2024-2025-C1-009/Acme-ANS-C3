
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
public class CrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(memberId);

		FlightAssignment assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setCrewMember(member);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		boolean hasLeg = assignment.getLeg() != null;
		super.state(hasLeg, "leg", "acme.validation.leg.required");

		if (hasLeg) {
			Date now = MomentHelper.getCurrentMoment();

			// Leg en el futuro (salida futura)
			boolean legStartsInFuture = assignment.getLeg().getScheduledDeparture().after(now);
			super.state(legStartsInFuture, "leg", "acme.validation.legNotPast.message");

			// Leg publicada
			boolean legNotDraft = !assignment.getLeg().isDraftMode();
			super.state(legNotDraft, "leg", "acme.validation.legNotPublished.message");

			// Crew AVAILABLE
			boolean memberAvailable = assignment.getCrewMember() != null && assignment.getCrewMember().getFlightCrewMemberStatus().equals(FlightCrewMemberStatus.AVAILABLE);
			super.state(memberAvailable, "crewMember", "acme.validation.memberAvailableCreate.message");

			// No solapamiento con otras legs del mismo miembro
			boolean noOverlap = true;
			List<Leg> legs = this.repository.findLegsByMemberId(assignment.getCrewMember().getId(), assignment.getId()).stream().toList();

			for (Leg l : legs)
				if (this.overlaps(assignment.getLeg(), l)) {
					noOverlap = false;
					break;
				}
			super.state(noOverlap, "leg", "acme.validation.legCompatible.message");
		}

	}

	private boolean overlaps(final Leg a, final Leg b) {
		return a.getScheduledDeparture().before(b.getScheduledArrival()) && b.getScheduledDeparture().before(a.getScheduledArrival());
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		super.bindObject(assignment, "duty", "status", "remarks");
		assignment.setLeg(leg);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCrewMember(member);
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		List<Leg> legs = this.repository.findSelectableLegsForMember(super.getRequest().getPrincipal().getActiveRealm().getId(), assignment.getCrewMember().getAirline().getId(), MomentHelper.getCurrentMoment());

		if (legs == null || legs.isEmpty())
			legs = List.of();

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

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
