
package acme.features.flightCrewMember.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.forms.flightCrewMemberDashboard.FlightCrewMemberDashboard;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	private FlightCrewMemberDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		FlightCrewMemberDashboard dashboard = new FlightCrewMemberDashboard();

		List<String> lastFiveDestinations;
		Integer numberOfLegsLowIncident;
		Integer numberOfLegsMediumIncident;
		Integer numberOfLegsHighIncident;
		List<String> crewMembersLastLeg;
		Double average;
		Integer minimum;
		Integer maximum;
		Double standard;

		int idLogged = super.getRequest().getPrincipal().getActiveRealm().getId();

		lastFiveDestinations = this.repository.findLastDestinations(idLogged);
		if (lastFiveDestinations.isEmpty())
			lastFiveDestinations = new ArrayList<>();
		else if (lastFiveDestinations.size() >= 5)
			lastFiveDestinations = lastFiveDestinations.subList(0, 5);

		numberOfLegsLowIncident = this.repository.findLegsCountBySeverityLevelsLow();
		numberOfLegsMediumIncident = this.repository.findLegsCountBySeverityLevelsMedium();
		numberOfLegsHighIncident = this.repository.findLegsCountBySeverityLevelsHigh();

		crewMembersLastLeg = this.repository.findCrewNamesInLastLeg(idLogged).stream().map(fcm -> fcm.getIdentity().getFullName().replace(",", "-")).toList();

		List<String> faConfirmed = this.repository.findFlightAssignmentsByCrewMember(idLogged, AssignmentStatus.CONFIRMED).stream().map(fa -> fa.getLeg().getLabel()).toList();
		List<String> faPending = this.repository.findFlightAssignmentsByCrewMember(idLogged, AssignmentStatus.CANCELLED).stream().map(fa -> fa.getLeg().getLabel()).toList();
		List<String> faDenied = this.repository.findFlightAssignmentsByCrewMember(idLogged, AssignmentStatus.PENDING).stream().map(fa -> fa.getLeg().getLabel()).toList();

		Date startDate = MomentHelper.getCurrentMoment();
		Date endDate = MomentHelper.getCurrentMoment();
		startDate.setMonth(startDate.getMonth() - 1);
		startDate.setDate(1);
		endDate.setMonth(startDate.getMonth() - 1);
		endDate.setDate(30);
		List<Long> counts = this.repository.getDailyAssignmentCounts(startDate, endDate, idLogged);
		minimum = counts.stream().min(Long::compare).orElse(0L).intValue();
		maximum = counts.stream().max(Long::compare).orElse(0L).intValue();
		average = counts.stream().mapToLong(Long::longValue).average().orElse(0.0);
		standard = Math.sqrt(counts.stream().mapToDouble(c -> Math.pow(c - average, 2)).average().orElse(0.0));

		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setNumberOfLegsLowIncident(numberOfLegsLowIncident);
		dashboard.setNumberOfLegsMediumIncident(numberOfLegsMediumIncident);
		dashboard.setNumberOfLegsHighIncident(numberOfLegsHighIncident);
		dashboard.setCrewMembersLastLeg(crewMembersLastLeg);
		dashboard.setConfirmedFlightAssignments(faConfirmed);
		dashboard.setPendingFlightAssignments(faPending);
		dashboard.setCancelledFlightAssignments(faDenied);
		dashboard.setAverage(average);
		dashboard.setMinimum(minimum);
		dashboard.setMaximum(maximum);
		dashboard.setStandard(standard);

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final FlightCrewMemberDashboard dashboard) {

		Dataset data = super.unbindObject(dashboard, "lastFiveDestinations", "numberOfLegsLowIncident", "numberOfLegsMediumIncident", "numberOfLegsHighIncident", "crewMembersLastLeg", "confirmedFlightAssignments", "pendingFlightAssignments",
			"cancelledFlightAssignments", "average", "minimum", "maximum", "standard");

		super.getResponse().addData(data);

	}

}
