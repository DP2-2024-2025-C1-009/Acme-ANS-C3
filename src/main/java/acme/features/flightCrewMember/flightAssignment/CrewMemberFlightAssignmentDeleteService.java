
package acme.features.flightCrewMember.flightAssignment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class CrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		String method = super.getRequest().getMethod();

		boolean isPublished = false;
		boolean falseDelete = false;
		Integer fId;
		boolean exists = false;
		boolean isOwner = false;

		int member = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			falseDelete = true;
			fId = super.getRequest().getData("id", Integer.class);
			if (fId != null) {
				FlightCrewMember memberLoged = this.repository.findMemberById(member);
				FlightAssignment fSelected = this.repository.findAssignmentById(fId);
				exists = fSelected != null;
				if (exists) {
					isOwner = fSelected.getCrewMember() == memberLoged;
					if (method.equals("GET"))
						isPublished = !fSelected.isDraftMode();
				}
			}
		}

		super.getResponse().setAuthorised(isOwner && !isPublished && falseDelete);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "lastUpdate", "status", "remarks", "leg");

	}

	@Override
	public void perform(final FlightAssignment assignment) {
		List<ActivityLog> logs = this.repository.findRelatedLogs(assignment.getId());
		if (assignment.isDraftMode() && !logs.isEmpty())
			this.repository.deleteAll(logs);
		this.repository.delete(assignment);

	}

	@Override
	public void validate(final FlightAssignment assignment) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		List<Leg> legs = this.repository.findAllLegs();
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");

		data.put("readonly", false);
		data.put("dutyChoices", dutyChoices);
		data.put("update", MomentHelper.getCurrentMoment());
		data.put("statusChoices", statusChoices);
		data.put("leg", legChoices.getSelected().getKey());
		data.put("legChoices", legChoices);
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());
		data.put("confirmation", false);

		super.getResponse().addData(data);
	}
}
