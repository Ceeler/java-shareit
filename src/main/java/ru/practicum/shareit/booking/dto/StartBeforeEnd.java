package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = StartBeforeEndValidator.class)
@Documented
public @interface StartBeforeEnd {
    String message() default "Start booking must be before end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
