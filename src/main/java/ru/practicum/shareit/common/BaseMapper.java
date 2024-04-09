package ru.practicum.shareit.common;

import java.util.List;

public interface BaseMapper<T, V> {

    V toDto(T model);

    T toModel(V dto);

    List<V> toDtoList(List<T> tList);

    void updateModelFromDto(T model, V dto);
}
