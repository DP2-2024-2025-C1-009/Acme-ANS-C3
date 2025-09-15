
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
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		for (Leg leg : legs) {
			boolean isFuture = leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
			boolean isAssigned = this.repository.isAlreadyAssignedToLeg(member, leg);
			boolean overlaps = this.repository.isOverlappingAssignment(member, leg.getScheduledDeparture(), leg.getScheduledArrival());
			boolean currentLeg = leg.equals(assignment.getLeg());

			if (isFuture && !isAssigned && !overlaps && !leg.isDraftMode() || currentLeg) {
				String key = Integer.toString(leg.getId());
				String label = leg.getFlightNumber();
				boolean selected = currentLeg;
				legChoices.add(key, label, selected);
			}
		}

		legChoices.add("0", "----", assignment.getLeg() == null);

		Dataset data = super.unbindObject(assignment, "duty", "lastUpdate", "status", "remarks", "draftMode", "leg");

		data.put("crewMember", member.getIdentity().getFullName());

		data.put("dutyChoices", dutyChoices);
		data.put("duty", dutyChoices.getSelected().getKey());

		data.put("statusChoices", statusChoices);
		data.put("status", statusChoices.getSelected().getKey());

		data.put("legChoices", legChoices);
		data.put("leg", assignment.getLeg() != null ? String.valueOf(assignment.getLeg().getId()) : "0");

		super.getResponse().addData(data);

		boolean showActivityLogs = !assignment.getDraftMode() && assignment.getLeg() != null && assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
		super.getResponse().addGlobal("canShowActivityLogs", showActivityLogs);
	}

}
