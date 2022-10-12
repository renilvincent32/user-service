package org.fimohealth.user.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReadOnlyValidator implements ConstraintValidator<ReadOnly, Integer> {

    @Override
    public boolean isValid(Integer age, ConstraintValidatorContext constraintValidatorContext) {
        return age == null;
    }
}
