
package acme.features.assistanceAgent.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.datatypes.ClaimStatus;
import acme.entities.agents.Claim;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	//Internal state ---------------------------------------------------------
	private final AssistanceAgentDashboardRepository repository;


	@Autowired
	public AssistanceAgentDashboardShowService(final AssistanceAgentDashboardRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		int agentId = principal.getId();
		Collection<Claim> claims = this.repository.findClaimsByAssistanceAgentId(agentId).stream().filter(c -> !c.getCIsAccepted()).toList();

		AssistanceAgentDashboard dashboard = new AssistanceAgentDashboard();
		//initial data
		dashboard.setRatioOfClaimsStoredSuccessfully(Double.NaN);
		dashboard.setRatioOfClaimsRejected(Double.NaN);
		dashboard.setAvgNumberOfLogsClaimsHave(Double.NaN);
		dashboard.setMaxNumberClaimsAssistedDuringLastMonth(0.0);
		dashboard.setMaxNumberOfLogsClaimsHave(0.0);
		dashboard.setMinNumberClaimsAssistedDuringLastMonth(0.0);
		dashboard.setMinNumberOfLogsClaimsHave(0.0);
		dashboard.setDevNumberClaimsAssistedDuringLastMonth(Double.NaN);
		dashboard.setDevNumberOfLogsClaimsHave(Double.NaN);
		dashboard.setAvgNumberClaimsAssistedDuringLastMonth(Double.NaN);

		if (!claims.isEmpty()) {
			//ratioOfClaimsStoredSuccessfully
			Long claimsResueltas = claims.stream().filter(x -> x.getStatus() == ClaimStatus.ACCEPTED).count();
			Double claimsResolvedRatio = (double) claimsResueltas / claims.size();
			dashboard.setRatioOfClaimsStoredSuccessfully(claimsResolvedRatio);
			//ratioOfClaimsRejected
			Long claimsRejected = claims.stream().filter(x -> x.getStatus() == ClaimStatus.REJECTED).count();
			Double claimsRejectedRatio = (double) claimsRejected / claims.size();
			dashboard.setRatioOfClaimsRejected(claimsRejectedRatio);

			//topThreeMonthsHighestNumberOfClaims
			List<Integer> topMonths = this.repository.topThreeMonthsHighestNumberOfClaimsByAgent(agentId, Pageable.ofSize(3));
			dashboard.setTopThreeMonthsHighestNumberOfClaims(topMonths);

			//avgNumberOfLogsClaimsHave
			long totalLogs = claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).sum();

			double averageLogsPerClaim = !claims.isEmpty() ? (double) totalLogs / claims.size() : 0.0;
			dashboard.setAvgNumberOfLogsClaimsHave(averageLogsPerClaim);

			//minNumberOfLogsClaimsHave
			Double minLogs = (double) claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).min().orElse(0);
			dashboard.setMinNumberOfLogsClaimsHave(minLogs);

			//maxNumberOfLogsClaimsHave
			Double maxLogs = (double) claims.stream().mapToLong(claim -> this.repository.countLogsByClaimId(claim.getId())).max().orElse(0);
			dashboard.setMaxNumberOfLogsClaimsHave(maxLogs);

			//devNumberOfLogsClaimsHave
			List<Long> logsPerClaim = claims.stream().map(claim -> this.repository.countLogsByClaimId(claim.getId())).toList();
			double mean = logsPerClaim.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
			double variance = logsPerClaim.stream().mapToDouble(logs -> Math.pow(logs - mean, 2)).average().orElse(0.0);
			double standardDeviationLogsPerClaim = Math.sqrt(variance);
			dashboard.setDevNumberOfLogsClaimsHave(standardDeviationLogsPerClaim);

			Collection<AssistanceAgent> agents = this.repository.findAllAssistanceAgent();
			Date fecha = MomentHelper.getCurrentMoment();
			fecha.setMonth(fecha.getMonth() - 1); // mes anterior

			// Por cada agente, cuenta sus claims del mes pasado
			List<Long> claimsPerAgentLastMonth = agents.stream()
				.map(agent -> this.repository.findClaimsByAssistanceAgentId(agent.getId()).stream().filter(c -> c.getStatus() != ClaimStatus.PENDING && !c.getCIsAccepted()).filter(c -> c.getRegistrationMoment().after(fecha)).count()).toList();

			// Calcula las métricas
			double avg = claimsPerAgentLastMonth.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
			double min = claimsPerAgentLastMonth.stream().mapToDouble(Long::doubleValue).min().orElse(0.0);
			double max = claimsPerAgentLastMonth.stream().mapToDouble(Long::doubleValue).max().orElse(0.0);
			double std = Math.sqrt(claimsPerAgentLastMonth.stream().mapToDouble(x -> Math.pow(x - avg, 2)).average().orElse(0.0));

			// Asignación al dashboard
			dashboard.setAvgNumberClaimsAssistedDuringLastMonth(avg);
			dashboard.setMinNumberClaimsAssistedDuringLastMonth(min);
			dashboard.setMaxNumberClaimsAssistedDuringLastMonth(max);
			dashboard.setDevNumberClaimsAssistedDuringLastMonth(std);

		}

		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final AssistanceAgentDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "ratioOfClaimsStoredSuccessfully", "ratioOfClaimsRejected", "topThreeMonthsHighestNumberOfClaims", "avgNumberOfLogsClaimsHave", "minNumberOfLogsClaimsHave", "maxNumberOfLogsClaimsHave",
			"devNumberOfLogsClaimsHave", "avgNumberClaimsAssistedDuringLastMonth", "minNumberClaimsAssistedDuringLastMonth", "maxNumberClaimsAssistedDuringLastMonth", "devNumberClaimsAssistedDuringLastMonth");

		super.getResponse().addData(dataset);
	}

}
