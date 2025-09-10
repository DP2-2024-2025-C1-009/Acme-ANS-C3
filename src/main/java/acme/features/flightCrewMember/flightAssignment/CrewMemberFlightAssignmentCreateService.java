
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class CrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(memberId);

		if (member == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("id")) {
			Integer id = super.getRequest().getData("id", Integer.class, 0);
			if (id != 0) {
				super.getResponse().setAuthorised(false);
				return;
			}
		}

		Object legData = super.getRequest().getData().get("leg");
		if (legData == null || "0".equals(legData.toString().trim())) {
			super.getResponse().setAuthorised(true);
			return;
		}

		String legKey = legData.toString().trim();
		if (legKey.matches("\\d+")) {
			int legId = Integer.parseInt(legKey);
			Leg leg = this.repository.findLegById(legId);
			boolean legValid = leg != null && !leg.isDraftMode();
			super.getResponse().setAuthorised(legValid);
			return;
		}

		super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		FlightAssignment assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setCrewMember(member);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(assignment);
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		int id = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(id);

		if (assignment.getLeg() != null) {

			Date start = assignment.getLeg().getScheduledDeparture();
			Date end = assignment.getLeg().getScheduledArrival();

			boolean legPast = assignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
			super.state(!legPast, "leg", "acme.validation.flightAssignment.leg.moment");

			boolean overlaps = this.repository.isOverlappingAssignment(member, start, end);
			super.state(!overlaps, "*", "acme.validation.flightAssignment.crewMember.multipleLegs");
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		super.bindObject(assignment, "duty", "status", "remarks");
		assignment.setLeg(leg);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCrewMember(member);
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		FlightCrewMember member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		Collection<Leg> legs = this.repository.findAllLegsByAirlineId(member.getAirline().getId());

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
				String label = leg.getFlightNumber();
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
		data.put("duty", dutyChoices.getSelected().getKey());
		data.put("dutyChoices", dutyChoices);
		data.put("statusChoices", statusChoices);
		data.put("status", statusChoices.getSelected().getKey());
		data.put("legChoices", legChoices);
		data.put("leg", legChoices.getSelected().getKey());
		data.put("crewMember", member.getIdentity().getFullName());

		super.getResponse().addData(data);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
