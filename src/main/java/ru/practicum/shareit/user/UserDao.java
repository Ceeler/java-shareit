package ru.practicum.shareit.user;

import ru.practicum.shareit.common.BaseDao;

import java.util.Optional;

public interface UserDao extends BaseDao<User, Integer> {

    Optional<User> findByEmail(String email);
}
