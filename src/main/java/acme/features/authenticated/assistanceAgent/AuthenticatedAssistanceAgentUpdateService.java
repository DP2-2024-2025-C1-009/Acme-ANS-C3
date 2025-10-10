/*
 * AuthenticatedAssistanceAgentUpdateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.authenticated.assistanceAgent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentUpdateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	private final AuthenticatedAssistanceAgentRepository repository;


	@Autowired
	public AuthenticatedAssistanceAgentUpdateService(final AuthenticatedAssistanceAgentRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------ç

	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findAssistanceAgentByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final AssistanceAgent object) {
		super.bindObject(object, "employeeCode", "spokenLanguages", "bio", "salary", "photoLink", "airline");
	}

	@Override
	public void validate(final AssistanceAgent object) {
		// Intentionally blank
	}

	@Override
	public void perform(final AssistanceAgent object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {
		Dataset dataset;
		SelectChoices choices;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		choices = SelectChoices.from(airlines, "iataCode", object.getAirline());

		dataset = super.unbindObject(object, "employeeCode", "spokenLanguages", "startWorking", "bio", "salary", "photoLink", "airline");
		dataset.put("airlines", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
