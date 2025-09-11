
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activityLog.ActivityLog;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (activityLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean fechaTrasLeg = MomentHelper.isAfterOrEqual(activityLog.getRegistrationMoment(), activityLog.getActivityLogAssignment().getLeg().getScheduledDeparture());
			super.state(context, fechaTrasLeg, "fechaActivityLog", "acme.validation.activityLog.beforeLeg");
			boolean legStarted = activityLog.getActivityLogAssignment().getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
			super.state(context, legStarted, "leg", "acme.validation.activityLog.leg.not-started");
			boolean legPublished = activityLog.getActivityLogAssignment().getLeg().isDraftMode();
			super.state(context, !legPublished, "leg", "acme.validation.flightAssignment.legIsNotPublished");
		}
		result = !super.hasErrors(context);

		return result;
	}
}
