
package acme.features.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiController
public class CrewMemberFlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentListPlannedService	plannedListService;
	@Autowired
	private CrewMemberFlightAssignmentListCompletedService	completedListService;
	@Autowired
	private CrewMemberFlightAssignmentShowService			showService;
	@Autowired
	private CrewMemberFlightAssignmentCreateService			createService;
	@Autowired
	private CrewMemberFlightAssignmentUpdateService			updateService;
	@Autowired
	private CrewMemberFlightAssignmentPublishService		publishService;
	@Autowired
	private CrewMemberFlightAssignmentDeleteService			deleteService;


	@PostConstruct
	protected void setup() {
		super.addCustomCommand("list-planned", "list", this.plannedListService);
		super.addCustomCommand("list-completed", "list", this.completedListService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("delete", this.deleteService);
	}
}
