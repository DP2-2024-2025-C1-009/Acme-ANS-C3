
package acme.features.administrator.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.ClaimStatus;
import acme.entities.agents.TrackingLog;

@GuiService
public class AdministratorTrackingLogShowService extends AbstractGuiService<Administrator, TrackingLog> {

	@Autowired
	private AdministratorTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id;
		TrackingLog trackingLog;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(ClaimStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "updateMoment", "creationMoment", "steps", "resolutionPercentage", "resolution", "isPublished");
		dataset.put("statuses", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());

		if (trackingLog.getTrackingSteps() != null)
			super.getResponse().addGlobal("isClaimPublished", trackingLog.getTrackingSteps().getCIsAccepted());
		else
			super.getResponse().addGlobal("isClaimPublished", false);
		super.getResponse().addData(dataset);
	}
}
