package ru.practicum.shareit.common;

import java.util.List;

public interface BaseMapper<T, TDto> {

    TDto toDto(T model);

    T toModel(TDto dto);

    List<TDto> toDtoList(List<T> tList);

    void updateModelFromDto(T model, TDto dto);
}
