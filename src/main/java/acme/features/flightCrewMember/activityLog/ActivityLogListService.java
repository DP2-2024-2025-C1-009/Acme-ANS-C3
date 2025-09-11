
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.features.flightCrewMember.flightAssignment.CrewMemberFlightAssignmentRepository;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository			repository;

	@Autowired
	CrewMemberFlightAssignmentRepository	repositoryFCM;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repositoryFCM.findAssignmentById(id);

		boolean authorised = assignment != null && !assignment.getDraftMode() && super.getRequest().getPrincipal().hasRealm(assignment.getCrewMember()) && assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());

		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {

		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repositoryFCM.findAssignmentById(id);
		if (assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAct", true);
		Collection<ActivityLog> logs = this.repository.findLogsByAssignmentId(id);
		super.getBuffer().addData(logs);

		boolean draftModeAssignment = assignment.getDraftMode();
		super.getResponse().addGlobal("draftModeFlightAssignment", draftModeAssignment);

		super.getResponse().addGlobal("id", id);

	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");
		super.getResponse().addData(data);
	}

}
