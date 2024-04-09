package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseDao;

import java.util.*;

@Repository("UserMemoryDao")
public class UserDao implements BaseDao<User, Integer> {

    private final Map<Integer, User> users = new HashMap<>();

    private Integer userIdGenerator = 1;

    @Override
    public User save(User user) {
        final int id = userIdGenerator++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> get(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }
}
