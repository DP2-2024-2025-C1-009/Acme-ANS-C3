
package acme.entities.legs;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidFlightNumber;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "scheduledArrival"), @Index(columnList = "scheduledDeparture"), @Index(columnList = "aircraft_id"), @Index(columnList = "draftMode"), @Index(columnList = "flightNumber")
})
@ValidLeg
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidFlightNumber
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory
	@Valid
	@Automapped
	private LegStatus			status;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Derived attributes --------------------------------------------------------


	@Transient
	public Double getDuration() {
		Duration result = MomentHelper.computeDuration(this.getScheduledDeparture(), this.getScheduledArrival());
		return result.getSeconds() / 60.;
	}

	@Transient
	public String getLabel() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm");

		String departureCity = this.departureAirport.getCity();
		String departureCountry = this.departureAirport.getCountry();
		String arrivalCity = this.arrivalAirport.getCity();
		String arrivalCountry = this.arrivalAirport.getCountry();
		String departureTime = timeFormat.format(this.getScheduledDeparture());
		String arrivalTime = timeFormat.format(this.getScheduledArrival());

		return String.format("%s: %s - %s: %s %s - %s", departureCountry, departureCity, arrivalCountry, arrivalCity, departureTime, arrivalTime);

	}

	// Relationships -------------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft	aircraft;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

}
