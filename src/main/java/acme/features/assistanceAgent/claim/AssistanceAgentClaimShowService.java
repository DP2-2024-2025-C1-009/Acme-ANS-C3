
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.ClaimStatus;
import acme.datatypes.ClaimType;
import acme.entities.agents.Claim;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean authorised;

		int claimId;
		String claimIdRaw;
		Claim claim;

		authorised = false;

		if (super.getRequest().hasData("id")) {
			claimIdRaw = super.getRequest().getData("id", String.class);

			try {
				claimId = Integer.parseInt(claimIdRaw);
			} catch (NumberFormatException e) {
				claimId = -1;
			}
			claim = this.repository.findClaimById(claimId);
			authorised = claim != null && claim.getRegisteredBy() != null && super.getRequest().getPrincipal().hasRealm(claim.getRegisteredBy());
		}
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id;
		Claim claim;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices typeChoices, statusChoices, legChoices;
		Collection<Leg> legs;
		Date registrationMoment;

		registrationMoment = claim.getRegistrationMoment();

		if (registrationMoment == null)
			registrationMoment = MomentHelper.getCurrentMoment();
		legs = this.repository.findAllPublishedLegsBefore(registrationMoment);

		if (legs.isEmpty() && claim.getLeg() != null)
			legs.add(claim.getLeg());

		typeChoices = SelectChoices.from(ClaimType.class, claim.getType());
		statusChoices = SelectChoices.from(ClaimStatus.class, claim.getStatus());
		legChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "cIsAccepted");
		dataset.put("types", typeChoices);
		dataset.put("type", typeChoices.getSelected().getKey());
		dataset.put("statuses", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
