
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
public class AirlineShowService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AirlineRepository airlineRepository;


	@Override
	public void authorise() {
		boolean exists = false;
		boolean isAuthorised = false;

		Integer id = super.getRequest().getData("id", Integer.class);
		if (id != null) {
			Airline airline = this.airlineRepository.findAirlineById(id);
			exists = airline != null;
			isAuthorised = airline != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		}
		boolean authorised = exists && isAuthorised;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int airlineId = super.getRequest().getData("id", int.class);
		Airline result = this.airlineRepository.findAirlineById(airlineId);
		super.getBuffer().addData(result);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset data = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");

		SelectChoices typeOptions = SelectChoices.from(AirlineType.class, airline.getType());

		data.put("confirmation", false);
		data.put("types", typeOptions);

		super.getResponse().addData(data);
	}
}
