package ru.practicum.shareit.item.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = ItemUpdateValidValidator.class)
@Documented
public @interface ItemUpdateValid {
    String message() default "At least one field must be not null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
