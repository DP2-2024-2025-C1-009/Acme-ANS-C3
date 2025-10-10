
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.MessageHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.constraints.InternationalisationHelper;
import acme.datatypes.ClaimStatus;
import acme.entities.agents.Claim;

@GuiService
public class AdministratorClaimListService extends AbstractGuiService<Administrator, Claim> {

	@Autowired
	private AdministratorClaimRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		claims = this.repository.findAllPublishedClaims();
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		ClaimStatus status;

		status = claim.getStatus();
		dataset = super.unbindObject(claim, "registrationMoment", "type");
		dataset.put("status", claim.getStatus());
		dataset.put("cIsAccepted", InternationalisationHelper.internationalizeBoolean(claim.getCIsAccepted()));

		super.addPayload(dataset, claim, "registeredBy.employeeCode", "leg.flightNumber", "description", "passengerEmail");

		if (!status.equals(ClaimStatus.PENDING))
			dataset.put("payload", dataset.get("payload") + "|" + MessageHelper.getMessage("assistance-agent.claim.list.completed"));
		super.getResponse().addData(dataset);
	}

}
