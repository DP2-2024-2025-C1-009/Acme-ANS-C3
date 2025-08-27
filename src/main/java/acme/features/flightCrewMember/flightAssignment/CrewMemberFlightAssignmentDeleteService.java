
package acme.features.flightCrewMember.flightAssignment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class CrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		Integer id = super.getRequest().getData("id", Integer.class);
		if (id != null) {
			int principalId = super.getRequest().getPrincipal().getActiveRealm().getId();
			FlightAssignment fa = this.repository.findAssignmentById(id);
			authorised = fa != null && fa.getCrewMember() != null && fa.getCrewMember().getId() == principalId && fa.isDraftMode();
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
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		List<ActivityLog> logs = this.repository.findRelatedLogs(assignment.getId());
		if (!logs.isEmpty())
			this.repository.deleteAll(logs);
		this.repository.delete(assignment);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		FlightCrewMember member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode");

		data.put("legLabel", assignment.getLeg() != null ? assignment.getLeg().getFlightNumber() : "-");
		data.put("readonly", true);
		data.put("crewMember", member);
		data.put("name", member.getIdentity().getName() + " " + member.getIdentity().getSurname());

		super.getResponse().addData(data);
	}

}
