package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.practicum.shareit.exception.model.ExceptionMessage;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ExceptionMessage> onConstraintException(ConstraintViolationException e) {
        log.warn("Ошибка валидации", e);
        if (e.getConstraintViolations() != null) {
            List<ExceptionMessage> response = e.getConstraintViolations().stream()
                    .map(v -> new ExceptionMessage(v.getMessage()))
                    .collect(Collectors.toList());
            return response;
        }
        return Collections.singletonList(new ExceptionMessage(e.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionMessage onNotFoundException(NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

    @ExceptionHandler({NotAuthorizedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionMessage onNotAuthorizedException(NotAuthorizedException e) {
        log.warn("403 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

    @ExceptionHandler({NotAuthenticatedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage onNotAuthenticatedException(NotAuthenticatedException e) {
        log.warn("500 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionMessage handleThrowable(final Throwable e) {
        log.warn("500 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

}
