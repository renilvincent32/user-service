package org.fimohealth.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReadOnlyValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {

    String message() default "Field cannot be updated";

    Class <?> [] groups() default {};

    Class <? extends Payload> [] payload() default {};
}