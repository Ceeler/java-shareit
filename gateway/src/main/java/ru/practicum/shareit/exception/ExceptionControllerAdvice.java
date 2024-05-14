package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.ExceptionMessage;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ExceptionMessage> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<ExceptionMessage> response = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ExceptionMessage(error.getField() + ": " + error.getDefaultMessage()))
                .collect(Collectors.toList());
        return response;
    }

    @ExceptionHandler({NotAuthenticatedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage onNotAuthenticatedException(NotAuthenticatedException e) {
        log.warn("500 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage onIllegalArgumentException(final Throwable e) {
        log.warn("400 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionMessage handleThrowable(final Throwable e) {
        log.warn("500 {}", e.getMessage());
        return new ExceptionMessage(e.getMessage());
    }

}
