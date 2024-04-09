package ru.practicum.shareit.common;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T, K> {

    T save(T t);

    Optional<T> get(K id);

    List<T> getAll();

    T update(T t);

    void delete(K id);

}
