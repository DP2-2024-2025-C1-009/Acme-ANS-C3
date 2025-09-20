
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;

@GuiService
public class AircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AircraftRepository aircraftRepository;


	@Override
	public void authorise() {
		boolean isAuthorised = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(Administrator.class)) {
			if (super.getRequest().getMethod().equals("GET"))
				isAuthorised = true;
			if (super.getRequest().getMethod().equals("POST") && super.getRequest().getData("id", Integer.class) != null)
				isAuthorised = super.getRequest().getData("id", Integer.class).equals(0);
		}

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		aircraft = new Aircraft();
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

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.aircraftRepository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices airlinesChoices = SelectChoices.from(this.aircraftRepository.findAllAirlines(), "name", aircraft.getAirline());
		Dataset data = super.unbindObject(aircraft, "model", "numberRegistration", "numberPassengers", "loadWeight", "isActive", "optionalDetails", "airline");
		data.put("airlinesChoices", airlinesChoices);
		data.put("airline", airlinesChoices.getSelected().getKey());
		data.put("confirmation", false);
		super.getResponse().addData(data);
	}

}
