package org.fimohealth.user.validation;

import org.fimohealth.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailUniqueValidator implements ConstraintValidator<Unique, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
       return !userRepository.findByEmail(email).isPresent();
    }
}
