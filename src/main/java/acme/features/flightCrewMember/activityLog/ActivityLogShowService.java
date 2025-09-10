
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {

		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		boolean authorised = log != null && super.getRequest().getPrincipal().hasRealm(log.getActivityLogAssignment().getCrewMember());

		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");
		boolean draftMode = this.repository.findAssignmentById(log.getActivityLogAssignment().getId()).getDraftMode();

		if (log.getActivityLogAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		super.getResponse().addGlobal("draftMode", draftMode);

		super.getResponse().addData(data);
	}
}
