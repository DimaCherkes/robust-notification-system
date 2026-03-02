package com.dmytrocherkes.iamservice.security.validation;

import com.dmytrocherkes.iamservice.model.request.RegistrationUserRequest;
import com.dmytrocherkes.iamservice.utils.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationUserRequest> {

    @Override
    public boolean isValid(RegistrationUserRequest request, ConstraintValidatorContext context) {
        return request.getPassword().equals(request.getConfirmPassword());
    }
}
