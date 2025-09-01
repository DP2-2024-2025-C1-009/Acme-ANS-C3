
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.LegStatus;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {

		boolean fakeUpdate = true;
		boolean exists = false;
		boolean isOwner = false;
		boolean isPublished = false;

		if (super.getRequest().hasData("id")) {
			int id = super.getRequest().getData("id", int.class);
			if (id != 0)
				fakeUpdate = false;
		}
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			Integer assignment = super.getRequest().getData("assignment", Integer.class);
			if (assignment != null) {
				FlightCrewMember member = this.repository.findMemberById(memberId);
				List<FlightAssignment> allFA = this.repository.findAllAssignments();
				FlightAssignment assignmentSelected = this.repository.findAssignmentById(assignment);
				exists = assignmentSelected != null || allFA.contains(assignmentSelected) && assignmentSelected != null;
				if (exists) {
					isOwner = assignmentSelected.getCrewMember() == member;
					isPublished = !assignmentSelected.getDraftMode();
				}
			}
		}
		super.getResponse().setAuthorised(fakeUpdate && isOwner && isPublished);
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
		log.setRegistrationMoment(MomentHelper.getCurrentMoment());

	}

	@Override
	public void validate(final ActivityLog log) {

		boolean completed = false;
		boolean notCancelled = true;

		FlightAssignment assignment = this.repository.findAssignmentById(super.getRequest().getData("assignmentId", Integer.class));

		if (assignment.getLeg().getStatus().equals(LegStatus.LANDED) || assignment.getLeg().getStatus().equals(LegStatus.CANCELLED))
			completed = true;
		super.state(completed, "*", "acme.validation.activityLogNotCompleted.message");

		if (assignment.getStatus().equals(AssignmentStatus.CANCELLED) || assignment.getStatus().equals(AssignmentStatus.PENDING))
			notCancelled = false;
		super.state(notCancelled, "*", "acme.validation.activityLogNotCancelled.message");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);

		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final ActivityLog log) {
		this.repository.save(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		data.put("assignmentId", log.getActivityLogAssignment().getId());

		super.getResponse().addData(data);
	}

}
