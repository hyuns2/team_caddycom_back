package com.flash21.caddycom.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private String regexp;
    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
       this.regexp = constraintAnnotation.regexp();
    }
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Pattern.matches(regexp, s);
    }
}
