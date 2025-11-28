package br.dev.kajosama.dropship.domain.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.dev.kajosama.dropship.domain.validators.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * @author Sam_Umbra
 * @Description Custom annotation to validate phone numbers using Google's
 *              libphonenumber library.
 *              This annotation can be applied to fields of type {@code String}
 *              to ensure they represent
 *              a valid phone number for a specified region.
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    /**
     * The error message to be displayed when the phone number is invalid.
     *
     * @return The default error message.
     */
    String message() default "Invalid phone number";

    /**
     * Allows the specification of validation groups, to which this constraint
     * belongs.
     *
     * @return An array of validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to attach custom payload
     * objects
     * to a constraint.
     *
     * @return An array of payload classes.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The region code to use for phone number parsing. Defaults to "BR" (Brazil).
     *
     * @return The region code as a String.
     */
    String region() default "BR";
}
