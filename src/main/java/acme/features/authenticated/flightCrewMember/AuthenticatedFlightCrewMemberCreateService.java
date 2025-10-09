
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberStatus;

@GuiService
public class AuthenticatedFlightCrewMemberCreateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		boolean authorised = !super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightCrewMember member = new FlightCrewMember();
		int id = super.getRequest().getPrincipal().getAccountId();

		UserAccount userAccount = this.repository.findUserAccountById(id);
		member.setUserAccount(userAccount);
		member.setEmployeeCode(GenerateCode.generate(member));

		super.getBuffer().addData(member);
	}

	@Override
	public void bind(final FlightCrewMember flightCrewMember) {
		super.bindObject(flightCrewMember, "employeeCode", "phoneNumber", "languageSkills", "flightCrewMemberStatus", "salary", "yearsOfExperience", "airline");
	}

	@Override
	public void validate(final FlightCrewMember flightCrewMember) {

	}

	@Override
	public void perform(final FlightCrewMember flightCrewMember) {
		this.repository.save(flightCrewMember);
	}

	@Override
	public void unbind(final FlightCrewMember flightCrewMember) {
		SelectChoices statusChoices = SelectChoices.from(FlightCrewMemberStatus.class, null);
		Collection<Airline> airlines = this.repository.finAllAirlines();
		SelectChoices legChoices = SelectChoices.from(airlines, "name", flightCrewMember.getAirline());

		Dataset dataset = super.unbindObject(flightCrewMember, "employeeCode", "phoneNumber", "languageSkills", "flightCrewMemberStatus", "salary", "yearsOfExperience", "airline");
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("airlineChoices", legChoices);
		dataset.put("airline", legChoices.getSelected().getKey());

		dataset.put("readOnly", false);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
