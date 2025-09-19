
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalScope;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {

	// Repositories ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean exists = false;
		boolean isAuthorised = false;

		Integer id = super.getRequest().getData("id", Integer.class);
		if (id != null) {
			Airport airport = this.repository.findAirportById(id);
			exists = airport != null;
			isAuthorised = airport != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		}
		boolean authorised = exists && isAuthorised;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id;
		Airport airport;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());
		dataset = super.unbindObject(airport, "airportName", "iataCode", "city", "country", "website", "email", "contactPhoneNumber", "operationalScope");
		dataset.put("operationalScope", choices);

		super.getResponse().addData(dataset);
	}

}
