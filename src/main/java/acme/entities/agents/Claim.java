
package acme.entities.agents;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.datatypes.ClaimStatus;
import acme.datatypes.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidClaim
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(past = true)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				cIsAccepted;

	// Relationships ----------------------------------------------------------

	@ManyToOne(optional = false)
	@Valid
	@Mandatory
	private AssistanceAgent		registeredBy;

	@ManyToOne(optional = false)
	@Valid
	@Mandatory
	private Leg					leg;


	@Transient
	public ClaimStatus getStatus() {
		ClaimStatus out;
		ClaimRepository claimRepository;
		Long acceptedLogs, rejectedLogs;

		out = ClaimStatus.PENDING;
		claimRepository = SpringHelper.getBean(ClaimRepository.class);

		acceptedLogs = claimRepository.findAmountOfTrackingLogsByClaimIdAndStatus(this.getId(), ClaimStatus.ACCEPTED);
		rejectedLogs = claimRepository.findAmountOfTrackingLogsByClaimIdAndStatus(this.getId(), ClaimStatus.REJECTED);

		if (acceptedLogs > 0L)
			out = ClaimStatus.ACCEPTED;
		else if (rejectedLogs > 0L)
			out = ClaimStatus.REJECTED;

		return out;
	}
}
