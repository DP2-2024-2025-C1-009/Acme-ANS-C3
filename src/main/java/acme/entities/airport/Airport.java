
package acme.entities.airport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAirport;
import acme.constraints.ValidPhoneNumber;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "airport")
@ValidAirport
public class Airport extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50, message = "{acme.validation.airport.airport-name-length}")
	@Automapped
	private String				airportName;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}$")
	@Column(unique = true)
	private String				iataCode;

	@Mandatory
	@Valid
	@Automapped
	private OperationalScope	operationalScope;

	@Mandatory
	@ValidString(min = 1, max = 50, message = "{acme.validation.airport.city-length}")
	@Automapped
	private String				city;

	@Mandatory
	@ValidString(min = 1, max = 50, message = "{acme.validation.airport.country-length}")
	@Automapped
	private String				country;

	@Optional
	@ValidUrl(remote = false, message = "{acme.validation.airport.website-valid}")
	@Automapped
	private String				website;

	@Optional
	@ValidEmail(message = "{acme.validation.airport.email-valid}")
	@Automapped
	private String				email;

	@Optional
	@ValidPhoneNumber
	@Automapped
	private String				contactPhoneNumber;

}
