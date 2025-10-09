
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airline.Airline;

@GuiService
public class AircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AircraftRepository aircraftRepository;


	@Override
	public void authorise() {
		boolean authorised;
		authorised = true;
		if (super.getRequest().hasData("id")) {
			Integer airlineId = super.getRequest().getData("airline", Integer.class);
			if (airlineId == null || airlineId != 0) {
				Airline airline = this.aircraftRepository.findAirlineById(airlineId);
				authorised = airline != null;
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.aircraftRepository.findAircraftById(id);
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "numberRegistration", "numberPassengers", "loadWeight", "isActive", "optionalDetails", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		Aircraft existingAircraft = this.aircraftRepository.findAircraftByNumberRegistration(aircraft.getNumberRegistration());
		boolean uniqueAircraft = existingAircraft == null || existingAircraft.equals(aircraft);
		super.state(uniqueAircraft, "numberRegistration", "acme.validation.numberRegistration");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.aircraftRepository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset data = super.unbindObject(aircraft, "model", "numberRegistration", "numberPassengers", "loadWeight", "isActive", "optionalDetails", "airline");

		SelectChoices airlinesChoices = SelectChoices.from(this.aircraftRepository.findAllAirlines(), "name", aircraft.getAirline());

		data.put("airlinesChoices", airlinesChoices);
		data.put("airline", airlinesChoices.getSelected().getKey());
		data.put("confirmation", false);

		super.getResponse().addData(data);
	}
}
