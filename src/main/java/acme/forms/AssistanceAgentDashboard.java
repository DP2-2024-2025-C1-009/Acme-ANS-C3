
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private Double				ratioOfClaimsStoredSuccessfully;
	private Double				ratioOfClaimsRejected;
	private List<Integer>		topThreeMonthsHighestNumberOfClaims;
	private Double				avgNumberOfLogsClaimsHave;
	private Double				minNumberOfLogsClaimsHave;
	private Double				maxNumberOfLogsClaimsHave;
	private Double				devNumberOfLogsClaimsHave;
	private Double				avgNumberClaimsAssistedDuringLastMonth;
	private Double				minNumberClaimsAssistedDuringLastMonth;
	private Double				maxNumberClaimsAssistedDuringLastMonth;
	private Double				devNumberClaimsAssistedDuringLastMonth;
}
