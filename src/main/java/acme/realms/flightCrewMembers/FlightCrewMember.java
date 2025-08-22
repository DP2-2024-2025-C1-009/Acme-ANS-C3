
package acme.realms.flightCrewMembers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidFlightCrewMember;
import acme.constraints.ValidPhoneNumber;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlightCrewMember
public class FlightCrewMember extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String					employeeCode;

	@Mandatory
	@ValidPhoneNumber
	private String					phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String					languageSkills;

	@Mandatory
	private FlightCrewMemberStatus	flightCrewMemberStatus;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money					salary;

	@Optional
	@ValidNumber(min = 0, max = 120)
	private Integer					yearsOfExperience;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	@JoinColumn(name = "airline_id", nullable = false)
	private Airline					airline;

}
