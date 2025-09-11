
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberStatus;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findActivityLogById(id);
		if (log != null) {
			boolean isOwner = super.getRequest().getPrincipal().hasRealm(log.getActivityLogAssignment().getCrewMember());
			boolean isDraft = log.getDraftMode();

			authorised = isOwner && isDraft;
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
		super.bindObject(log, "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog log) {
		FlightAssignment assignment = log.getActivityLogAssignment();

		boolean isPublished = !assignment.getDraftMode();
		super.state(isPublished, "*", "acme.validation.activityLog.flightAssignment-not-published");

		boolean legStarted = assignment.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
		super.state(legStarted, "*", "acme.validation.activityLog.leg.not-finished");

		boolean isAvailable = assignment.getCrewMember().getFlightCrewMemberStatus().equals(FlightCrewMemberStatus.AVAILABLE);
		super.state(isAvailable, "*", "acme.validation.flightAssignment.crewMember.available");

	}

	@Override
	public void perform(final ActivityLog log) {
		log.setDraftMode(false);
		this.repository.save(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		if (log.getActivityLogAssignment().getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()))
			super.getResponse().addGlobal("showAct", true);

		super.getResponse().addData(data);
	}

}
