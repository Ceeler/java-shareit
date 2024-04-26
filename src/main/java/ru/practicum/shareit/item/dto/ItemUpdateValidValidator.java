package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemUpdateValidValidator implements ConstraintValidator<ItemUpdateValid, ItemUpdateRequest> {

    @Override
    public boolean isValid(ItemUpdateRequest itemUpdateRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (itemUpdateRequest.getName() == null &&
            itemUpdateRequest.getDescription() == null &&
            itemUpdateRequest.getAvailable() == null) {
            return false;
        }

        if (itemUpdateRequest.getName() != null && itemUpdateRequest.getName().isBlank()) {
            return false;
        }

        if (itemUpdateRequest.getDescription() != null && itemUpdateRequest.getDescription().isBlank()) {
            return false;
        }
        return true;
    }
}
