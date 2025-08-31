
package acme.features.flightCrewMember.flightAssignment;

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
public class CrewMemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "status", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void perform(final FlightAssignment assignment) {
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
