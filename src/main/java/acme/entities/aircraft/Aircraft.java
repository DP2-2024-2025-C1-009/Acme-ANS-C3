
package acme.entities.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				model;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Column(unique = true)
	private String				numberRegistration;

	@Mandatory
	@ValidNumber(min = 1, max = 255)
	@Automapped
	private Integer				numberPassengers;

	@Mandatory
	@ValidNumber(min = 2000.0, max = 50000.0)
	@Automapped
	private Double				loadWeight;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				isActive;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				optionalDetails;

	// Relationships -------------------------------------------------------------

	@Mandatory //
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;

}
