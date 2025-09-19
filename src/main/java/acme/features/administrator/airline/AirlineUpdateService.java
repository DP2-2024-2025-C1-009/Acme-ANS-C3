
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AirlineRepository airlineRepository;


	@Override
	public void authorise() {
		Integer id = super.getRequest().getData("id", Integer.class);
		Airline airline = this.airlineRepository.findAirlineById(id);
		boolean authorised = airline != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(authorised);
	}
	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Airline airline = this.airlineRepository.findAirlineById(id);
		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.airlineRepository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset data = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		data.put("confirmation", false);
		data.put("types", SelectChoices.from(AirlineType.class, airline.getType()));
		super.getResponse().addData(data);
	}
}
