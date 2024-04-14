package ru.practicum.shareit.exception.model;

public class DaoException extends RuntimeException {
    public DaoException(String message) {
        super(message);
    }
}
