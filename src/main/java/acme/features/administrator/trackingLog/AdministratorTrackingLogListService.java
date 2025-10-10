
package acme.features.administrator.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.constraints.InternationalisationHelper;
import acme.datatypes.ClaimStatus;
import acme.entities.agents.TrackingLog;

@GuiService
public class AdministratorTrackingLogListService extends AbstractGuiService<Administrator, TrackingLog> {

	@Autowired
	private AdministratorTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int masterId;
		Collection<TrackingLog> trackingLogs;

		masterId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findAllTrackingLogsByMasterId(masterId);
		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "resolutionPercentage", "status");
		dataset.put("isPublished", InternationalisationHelper.internationalizeBoolean(trackingLog.getIsPublished()));

		super.addPayload(dataset, trackingLog, "updateMoment", "creationMoment", "resolution", "steps", "trackingSteps.id");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLogs) {
		int masterId;
		boolean canCreate;

		canCreate = trackingLogs.stream().filter(t -> !t.getStatus().equals(ClaimStatus.PENDING)).count() < 2L;
		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("canCreate", canCreate);
	}
}
