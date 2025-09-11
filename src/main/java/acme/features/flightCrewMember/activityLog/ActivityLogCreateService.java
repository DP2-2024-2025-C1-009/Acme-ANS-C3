
package acme.features.flightCrewMember.activityLog;

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
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository			repository;

	@Autowired
	CrewMemberFlightAssignmentRepository	repositoryFCM;


	@Override
	public void authorise() {
		boolean authorised = false;

		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repositoryFCM.findCrewMemberById(id);
		if (member != null) {
			Object assignmentData = super.getRequest().getData().get("assignmentId");

			if (assignmentData != null) {
				int assignmentId = Integer.parseInt(assignmentData.toString());
				FlightAssignment assignment = this.repositoryFCM.findAssignmentById(assignmentId);

				if (assignment != null) {
					boolean isOwner = assignment.getCrewMember().getId() == id;
					boolean isPublished = !assignment.getDraftMode();
					boolean legStarted = assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());

					authorised = isOwner && isPublished && legStarted;
				}
			}
		}
		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {
		ActivityLog log = new ActivityLog();

		int assignmentId = super.getRequest().getData("assignmentId", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
		log.setActivityLogAssignment(assignment);
		log.setRegistrationMoment(MomentHelper.getCurrentMoment());
		log.setDraftMode(true);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog log) {
		super.bindObject(log, "incidentType", "description", "severityLevel");

	}

	@Override
	public void validate(final ActivityLog log) {
		;
	}

	@Override
	public void perform(final ActivityLog log) {
		this.repository.save(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");
		boolean draftModeFlightAssignment = log.getActivityLogAssignment().getDraftMode();

		if (log.getActivityLogAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAct", true);

		super.getResponse().addGlobal("draftModeFlightAssignment", draftModeFlightAssignment);

		data.put("assignmentId", super.getRequest().getData("assignmentId", int.class));

		super.getResponse().addData(data);
	}

}
