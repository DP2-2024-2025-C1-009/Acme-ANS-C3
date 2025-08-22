
package acme.entities.flightAssignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	// Serialisation identifier

	private static final long	serialVersionUID	= 1L;

	// Attributes 

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@Automapped
	private Duty				duty;

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@ValidMoment(past = true, min = "2000/01/01 00:00", max = "2100/01/01 00:00", message = "{acme.validation.flightAssignment.Past}")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdate;

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@Automapped
	private AssignmentStatus	status;

	@Optional
	@ValidString(min = 0, max = 255, message = "{acme.validation.flightAssignment.remarks}")
	@Automapped
	private String				remarks;

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@Automapped
	private boolean				draftMode;

	// RelationShips

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@Valid
	@ManyToOne(optional = false)
	private FlightCrewMember	crewMember;

	@Mandatory(message = "{acme.validation.flightAssignment.NotNull}")
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;

}
