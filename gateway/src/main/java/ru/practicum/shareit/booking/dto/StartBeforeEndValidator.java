package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingCreateRequest> {
    @Override
    public boolean isValid(BookingCreateRequest bookingCreateRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingCreateRequest.getStart() == null || bookingCreateRequest.getEnd() == null) {
            return false;
        }
        return bookingCreateRequest.getStart().isBefore(bookingCreateRequest.getEnd());
    }
}
