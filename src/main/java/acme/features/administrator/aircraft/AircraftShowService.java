
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;

@GuiService
public class AircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AircraftRepository aircraftRepository;


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(Administrator.class)) {
			Integer aircraftId = super.getRequest().getData("id", Integer.class);

			if (super.getRequest().getMethod().equals("GET") && aircraftId != null) {
				Aircraft aircraft = this.aircraftRepository.findAircraftById(aircraftId);
				authorised = aircraft != null;
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft res = this.aircraftRepository.findAircraftById(id);
		super.getBuffer().addData(res);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices airlinesChoices;
		airlinesChoices = SelectChoices.from(this.aircraftRepository.findAllAirlines(), "name", aircraft.getAirline());

		Dataset data = super.unbindObject(aircraft, "model", "numberRegistration", "numberPassengers", "loadWeight", "isActive", "optionalDetails", "airline");
		data.put("confirmation", false);
		data.put("airlinesChoices", airlinesChoices);

		super.getResponse().addData(data);
	}
}
