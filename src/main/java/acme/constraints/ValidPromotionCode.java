
package acme.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Constraint(validatedBy = PromotionCodeValidator.class)
@Target({
	ElementType.FIELD
})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface ValidPromotionCode {

	String message() default "";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
