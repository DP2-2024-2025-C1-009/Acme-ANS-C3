
package acme.features.assistanceAgent.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentDashboardController extends AbstractGuiController<AssistanceAgent, AssistanceAgentDashboard> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentDashboardShowService showService;


	@Autowired
	public AssistanceAgentDashboardController(final AssistanceAgentDashboardShowService showService) {
		this.showService = showService;
	}

	// Constructors -----------------------------------------------------------

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}
}
