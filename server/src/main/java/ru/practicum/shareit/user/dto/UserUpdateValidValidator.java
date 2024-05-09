package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.dto.request.UserUpdateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UserUpdateValidValidator implements ConstraintValidator<UserUpdateValid, UserUpdateRequest> {

    private static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public boolean isValid(UserUpdateRequest userUpdateRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (userUpdateRequest.getEmail() == null && userUpdateRequest.getName() == null) {
            return false;
        }

        if (userUpdateRequest.getName() != null && userUpdateRequest.getName().isBlank()) {
            return false;
        }

        if (userUpdateRequest.getEmail() != null) {
            return Pattern.compile(EMAIL_PATTERN).matcher(userUpdateRequest.getEmail()).matches();
        }

        return true;
    }
}
