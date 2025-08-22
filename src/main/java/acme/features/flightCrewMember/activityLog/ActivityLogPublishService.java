
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {

		boolean hasRegistrationMoment = true;
		boolean isOwner = false;
		boolean exists = false;
		boolean isPublished = true;

		String method = super.getRequest().getMethod();

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (!super.getRequest().getData().isEmpty() && super.getRequest().getData() != null) {
			Integer alId = super.getRequest().getData("id", Integer.class);
			if (alId != null) {
				FlightCrewMember member = this.repository.findMemberById(memberId);
				List<ActivityLog> allFA = this.repository.findAllActivityLog();
				ActivityLog log = this.repository.findActivityLogById(alId);
				exists = log != null || allFA.contains(log) && log != null;
				hasRegistrationMoment = super.getRequest().hasData("registrationMoment");
				if (exists) {
					isOwner = log.getActivityLogAssignment().getCrewMember() == member;
					if (method.equals("GET"))
						isPublished = !log.isDraftMode();
				}
			}
		}

		super.getResponse().setAuthorised(isOwner && isPublished && hasRegistrationMoment);
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

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final ActivityLog log) {
		log.setDraftMode(false);
		this.repository.save(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode");

		data.put("assignmentId", log.getActivityLogAssignment().getId());

		super.getResponse().addData(data);
	}

}
