package org.fimohealth.user.domain;

import javax.validation.ConstraintViolation;

public class Error {

    private final String field;
    private final String message;

    public Error(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Builder method to build the Error instance from constraint violation obj
     * @param violation
     * @return
     */
    public static Error build(ConstraintViolation<?> violation) {
        return new Error(violation.getPropertyPath().toString(), violation.getMessage());
    }
}
