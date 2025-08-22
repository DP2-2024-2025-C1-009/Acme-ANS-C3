
package acme.features.flightCrewMember.flightAssignment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
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
		boolean isOwner = false;
		boolean exists = false;

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty()) {
			Integer assignmentId = super.getRequest().getData("id", Integer.class);
			if (assignmentId != null) {
				FlightCrewMember member = this.repository.findMemberById(memberId);

				FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
				exists = assignment != null;
				if (assignment != null)
					isOwner = assignment.getCrewMember() == member;
			}
		}
		boolean authorised = isOwner && exists;
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
		List<Leg> legs = this.repository.findAllLegs();
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");
		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("legRelated", legChoices.getSelected().getKey());
		data.put("leg", legChoices);
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());
		data.put("flightAssignment", assignment.getId());
		super.getResponse().addData(data);
	}

}
