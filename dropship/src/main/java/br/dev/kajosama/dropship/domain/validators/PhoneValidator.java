package br.dev.kajosama.dropship.domain.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import br.dev.kajosama.dropship.domain.interfaces.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private String region;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.region = constraintAnnotation.region();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isEmpty()) return true; // ignora nulos

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phone, region);
            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }

    // +5511912345678
    public static String normalizeToE164(String phone, String region) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber number = phoneUtil.parse(phone, region);
        if (!phoneUtil.isValidNumber(number)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Número inválido");
        }
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}