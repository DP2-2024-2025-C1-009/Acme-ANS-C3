
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		if (log != null) {
			int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
			FlightAssignment assignment = log.getActivityLogAssignment();

			boolean isOwner = assignment.getCrewMember().getId() == userId;
			boolean isPublished = !assignment.getDraftMode();
			boolean isDraft = log.getDraftMode();

			authorised = isOwner && isDraft && isPublished;
		}

		super.getResponse().setAuthorised(authorised);

	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog log) {
		;
	}

	@Override
	public void validate(final ActivityLog log) {
		;
	}

	@Override
	public void perform(final ActivityLog log) {
		this.repository.delete(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (log.getActivityLogAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAction", true);

		super.getResponse().addData(data);
	}

}
