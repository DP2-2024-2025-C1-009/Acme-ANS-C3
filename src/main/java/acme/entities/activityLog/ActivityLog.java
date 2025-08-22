
package acme.entities.activityLog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flightAssignment.FlightAssignment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation identifier

	private static final long	serialVersionUID	= 1L;

	// Attributes

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@ValidMoment(past = true, min = "2000/01/01 00:00", max = "2100/01/01 00:00", message = "{acme.validation.activityLog.Past}")
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@ValidString(min = 1, max = 50, message = "{acme.validation.activityLog.incidentType}")
	@Automapped
	private String				incidentType;

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@ValidString(min = 1, max = 255, message = "{acme.validation.activityLog.description}")
	@Automapped
	private String				description;

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@ValidNumber(min = 0, max = 10, message = "{acme.validation.activityLog.severityLevel}")
	@Automapped
	private Integer				severityLevel;

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@Automapped
	private boolean				draftMode;

	// RelationShips

	@Mandatory(message = "{acme.validation.activityLog.NotNull}")
	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_assignment_id", nullable = false)
	private FlightAssignment	activityLogAssignment;
}
