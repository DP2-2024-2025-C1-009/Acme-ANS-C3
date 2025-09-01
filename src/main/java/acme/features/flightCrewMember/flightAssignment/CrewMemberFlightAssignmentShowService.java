
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
import acme.entities.legs.LegStatus;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberStatus;

@GuiService
public class CrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Integer assignmentId = super.getRequest().getData("id", Integer.class);
		FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
		boolean authorised = assignment != null && super.getRequest().getPrincipal().hasRealm(assignment.getCrewMember());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(member.getAirline().getId());
		SelectChoices legChoices = new SelectChoices();
		boolean hasAvailableLegs = false;
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices availabilityChoices = SelectChoices.from(FlightCrewMemberStatus.class, assignment.getCrewMember().getFlightCrewMemberStatus());
		SelectChoices legStatuses = SelectChoices.from(LegStatus.class, assignment.getLeg() != null ? assignment.getLeg().getStatus() : null);

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

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode", "leg");
		if (assignment.getCrewMember() != null) {
			member = assignment.getCrewMember();
			data.put("flightCrewMember.id", member.getId());
			data.put("flightCrewMember.employeeCode", member.getEmployeeCode());
			data.put("flightCrewMember.phoneNumber", member.getPhoneNumber());
			data.put("flightCrewMember.languageSkills", member.getLanguageSkills());
			data.put("flightCrewMember.availabilityStatus", member.getFlightCrewMemberStatus());
			data.put("flightCrewMember.salary", member.getSalary());
			data.put("flightCrewMember.yearsOfExperience", member.getYearsOfExperience());
			data.put("flightCrewMember.airline", member.getAirline().getName());
		}

		if (assignment.getLeg() != null) {
			Leg leg = assignment.getLeg();
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

		data.put("leg", assignment.getLeg() != null ? Integer.toString(assignment.getLeg().getId()) : "0");
		data.put("crewMember", member.getIdentity().getFullName());
		data.put("statusChoices", statusChoices);
		data.put("status", statusChoices.getSelected().getKey());
		data.put("duty", dutyChoices.getSelected().getKey());

		data.put("confirmation", false);
		data.put("dutyChoices", dutyChoices);
		data.put("availabilityChoices", availabilityChoices);
		data.put("legStatuses", legStatuses);
		data.put("legChoices", legChoices);

		boolean showActivityLogs = !assignment.getDraftMode() && assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
		super.getResponse().addGlobal("canShowActivityLogs", showActivityLogs);

		super.getResponse().addData(data);
	}

}
