
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

@GuiService
public class CrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(memberId);

		if (member == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class, 0);
			if (id != 0) {
				super.getResponse().setAuthorised(false);
				return;
			}
		}

		Object legData = super.getRequest().getData().get("leg");
		if (legData == null || "0".equals(legData.toString().trim())) {
			super.getResponse().setAuthorised(true);
			return;
		}

		String legKey = legData.toString().trim();
		if (legKey.matches("\\d+")) {
			int legId = Integer.parseInt(legKey);
			Leg leg = this.repository.findLegById(legId);
			boolean legValid = leg != null && !leg.isDraftMode();
			super.getResponse().setAuthorised(legValid);
			return;
		}

		super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		FlightAssignment assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setCrewMember(member);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(assignment);
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(id);

		if (assignment.getLeg() != null) {

			Date start = assignment.getLeg().getScheduledDeparture();
			Date end = assignment.getLeg().getScheduledArrival();

			boolean legPast = assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(!legPast, "leg", "acme.validation.flightAssignment.leg.moment");

			boolean overlaps = this.repository.isOverlappingAssignment(member, start, end);
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

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
		List<Leg> legs = this.repository.findSelectableLegs(MomentHelper.getCurrentMoment());

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
