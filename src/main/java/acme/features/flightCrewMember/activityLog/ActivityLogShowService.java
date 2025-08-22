
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {

		boolean exists = false;
		boolean isOwner = false;

		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty()) {
			Integer log = super.getRequest().getData("id", Integer.class);
			if (log != null) {
				FlightCrewMember member = this.repository.findMemberById(id);
				List<FlightAssignment> allFA = this.repository.findAllAssignments();
				ActivityLog logSelected = this.repository.findActivityLogById(log);
				exists = logSelected != null || allFA.contains(logSelected);
				if (logSelected != null)
					isOwner = logSelected.getActivityLogAssignment().getCrewMember() == member;
			}
		}

		super.getResponse().setAuthorised(isOwner);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode", "activityLogAssignment");
		super.getResponse().addData(data);
	}
}
