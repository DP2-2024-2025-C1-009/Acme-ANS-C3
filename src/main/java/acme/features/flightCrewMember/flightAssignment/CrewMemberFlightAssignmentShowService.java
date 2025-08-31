
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
public class CrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Integer assignmentId = super.getRequest().getData("id", Integer.class);
		FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
		boolean authorised = assignment != null && super.getRequest().getPrincipal().hasRealm(assignment.getCrewMember());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		List<Leg> selectable = this.repository.findSelectableLegs(MomentHelper.getCurrentMoment());
		if (selectable == null)
			selectable = List.of();

		SelectChoices legChoices = SelectChoices.from(selectable, "flightNumber", assignment.getLeg());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");

		String legLabel = assignment.getLeg() != null ? assignment.getLeg().getFlightNumber() : "-";
		data.put("legLabel", legLabel);

		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("legChoices", legChoices);
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());
		data.put("flightAssignment", assignment.getId());

		super.getResponse().addData(data);
	}

}
