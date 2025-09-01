
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class CrewMemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", Integer.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);

		boolean authorised = false;

		if (assignment != null && assignment.getDraftMode()) {
			int principalId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean isOwner = assignment.getCrewMember().getId() == principalId;

			Object legData = super.getRequest().getData().get("leg");
			if (legData instanceof String legKey) {
				legKey = legKey.trim();

				if (legKey.equals("0"))
					authorised = isOwner;
				else if (legKey.matches("\\d+")) {
					int legId = Integer.parseInt(legKey);
					Leg leg = this.repository.findLegById(legId);
					boolean legValid = leg != null && !leg.isDraftMode() && this.repository.findAllLegs().contains(leg);
					authorised = isOwner && legValid;
				}
			}

		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "status", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(member.getAirline().getId());
		FlightAssignment flightAssignment = this.repository.findAssignmentById(assignment.getId());
		SelectChoices legChoices = new SelectChoices();
		boolean hasAvailableLegs = false;
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		for (Leg leg : legs) {
			boolean isFuture = leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			boolean isAssigned = this.repository.isAlreadyAssignedToLeg(member, leg);
			boolean overlaps = this.repository.isOverlappingAssignment(member, leg.getScheduledDeparture(), leg.getScheduledArrival());
			boolean currentLeg = leg.equals(assignment.getLeg());

			if (isFuture && !isAssigned && !overlaps && !leg.isDraftMode() || currentLeg) {
				String key = Integer.toString(leg.getId());
				String label = leg.getFlightNumber() + " (" + leg.getFlight().getTag() + ")";
				boolean selected = currentLeg;
				legChoices.add(key, label, selected);
				hasAvailableLegs = true;
			}
		}

		if (!hasAvailableLegs)
			legChoices.add("0", "acme.validation.flightAssignment.crewMember.noAvailableLegs", true);
		else
			legChoices.add("0", "----", assignment.getLeg() == null);

		Dataset data = super.unbindObject(assignment, "duty", "status", "moment", "remarks", "draftMode", "leg");

		data.put("duty", dutyChoices.getSelected().getKey());
		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("status", statusChoices.getSelected().getKey());
		data.put("legChoices", legChoices);
		data.put("leg", legChoices.getSelected().getKey());
		data.put("crewMember", member.getIdentity().getFullName());

		if (flightAssignment.getLeg() != null) {
			Leg leg = flightAssignment.getLeg();
			data.put("leg.id", leg.getId());
			data.put("leg.flightNumber", leg.getFlightNumber());
			data.put("leg.status", leg.getStatus());
			data.put("leg.scheduledDeparture", leg.getScheduledDeparture());
			data.put("leg.scheduledArrival", leg.getScheduledArrival());
			data.put("leg.departureAirport", leg.getDepartureAirport().getAirportName());
			data.put("leg.arrivalAirport", leg.getArrivalAirport().getAirportName());
			data.put("leg.aircraft", leg.getAircraft().getNumberRegistration());
			data.put("leg.flight", leg.getFlight().getTag());
		}

		super.getResponse().addData(data);
	}

}
