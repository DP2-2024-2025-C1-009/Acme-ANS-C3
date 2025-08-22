
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		if (!super.getRequest().getData().isEmpty()) {
			Integer faId = super.getRequest().getData("faId", Integer.class);
			if (faId != null) {
				int id = super.getRequest().getPrincipal().getActiveRealm().getId();
				FlightCrewMember member = this.repository.findMemberById(id);
				FlightAssignment assignment = this.repository.findAssignmentById(faId);
				if (assignment != null)
					authorised = assignment.getCrewMember() == member;
			}
		}

		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {

		int assignment = super.getRequest().getData("assignment", int.class);
		int member = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<ActivityLog> logs = this.repository.findLogsByAssignmentId(assignment, member);

		super.getResponse().addGlobal("assignment", assignment);

		super.getBuffer().addData(logs);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "incidentType", "description", "severityLevel");
		data.put("assignment", log.getActivityLogAssignment().getId());
		super.getResponse().addData(data);
	}

}
