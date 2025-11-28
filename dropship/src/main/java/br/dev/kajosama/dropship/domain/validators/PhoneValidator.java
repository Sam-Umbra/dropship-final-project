package br.dev.kajosama.dropship.domain.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
/**
 * Custom validator for phone numbers using Google's libphonenumber library.
 * This validator checks if a given string represents a valid phone number
 * for a specified region.
 * @author Sam_Umbra
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    /**
 * The default region code to use for phone number parsing if not specified.
 */
    private String region;

    /**
 * Initializes the validator with the region specified in the {@link ValidPhone} annotation.
 *
 * @param constraintAnnotation The annotation instance.
 */
    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.region = constraintAnnotation.region();
    }

    /**
 * Validates if the given phone number string is a valid number for the configured region.
 * Null or empty strings are considered valid (to allow optional phone numbers).
 *
 * @param phone The phone number string to validate.
 * @param context The context in which the constraint is evaluated.
 * @return True if the phone number is valid or empty/null, false otherwise.
 */
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isEmpty()) return true; // ignora nulos

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            /**
 * Parses the phone number string into a {@link Phonenumber.PhoneNumber} object.
 *
 * @param phone The phone number string.
 * @param region The region code to assist in parsing.
 * @return A parsed {@link Phonenumber.PhoneNumber} object.
 */
            Phonenumber.PhoneNumber number = phoneUtil.parse(phone, region);
            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }

    /**
 * Normalizes a phone number to the E.164 format (e.g., +5511912345678).
 * This method parses the phone number using the specified region and
 * formats it into the international E.164 standard.
 *
 * @param phone The phone number string to normalize.
 * @param region The region code to assist in parsing.
 * @return The phone number formatted in E.164.
 * @throws NumberParseException if the phone number cannot be parsed or is invalid.
 * @see <a href="https://en.wikipedia.org/wiki/E.164">E.164 Standard</a>
 * @see com.google.i18n.phonenumbers.PhoneNumberUtil#format(Phonenumber.PhoneNumber, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat)
 */
    public static String normalizeToE164(String phone, String region) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber number = phoneUtil.parse(phone, region);
        if (!phoneUtil.isValidNumber(number)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Número inválido");
        }
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}