
package acme.features.any.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;

@GuiService
public class AnyFlightAssignmentShowService extends AbstractGuiService<Any, FlightAssignment> {

	@Autowired
	private AnyFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Integer id = super.getRequest().getData("id", Integer.class);
		boolean authorised = id != null && this.repository.findPublishedById(id) != null;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "status", "remarks");

		String legCode = flightAssignment.getLeg() != null ? flightAssignment.getLeg().getFlightNumber() : "-";
		dataset.put("legCode", legCode);

		String crewMemberName = "-";
		if (flightAssignment.getCrewMember() != null)
			crewMemberName = flightAssignment.getCrewMember().getIdentity().getFullName();
		dataset.put("crewMemberName", crewMemberName);

		super.getResponse().addData(dataset);
	}

}
