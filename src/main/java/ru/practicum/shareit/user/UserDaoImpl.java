package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.DaoException;

import java.util.*;

@Repository("UserMemoryDao")
public class UserDaoImpl implements UserDao {

    private final Map<Integer, User> users = new HashMap<>();

    private final Map<String, User> userEmailMap = new HashMap<>();

    private Integer userIdGenerator = 0;

    @Override
    public User save(User user) {
        if (userEmailMap.containsKey(user.getEmail())) {
            throw new DaoException("Email is already used");
        }
        final int id = ++userIdGenerator;
        user.setId(id);
        users.put(id, user);
        userEmailMap.put(user.getEmail(), user);
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
        if (!users.containsKey(user.getId())) {
            throw new DaoException("No user to update");
        }

        User oldUser = users.get(user.getId());

        if (!user.getEmail().equals(oldUser.getEmail())) {
            if (userEmailMap.containsKey(user.getEmail())) {
                throw new DaoException("Email is already used");
            }
            userEmailMap.remove(oldUser.getEmail());
        }

        users.put(user.getId(), user);
        userEmailMap.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void delete(Integer id) {
        User user = users.get(id);
        users.remove(id);
        userEmailMap.remove(user.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userEmailMap.get(email));
    }
}
