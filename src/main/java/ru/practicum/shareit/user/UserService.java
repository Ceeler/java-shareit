package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserMapperImpl userMapper;

    public UserResponse getUser(Integer id) {
        User user = userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public List<UserResponse> getAll() {
        return userMapper.toDtoList(userDao.getAll());
    }

    public UserResponse saveUser(UserCreateRequest userDto) {
        User newUser = userMapper.toModel(userDto);
        if (!isEmailFree(newUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = userDao.save(newUser);
        return userMapper.toDto(user);
    }

    public UserResponse updateUser(Integer id, UserUpdateRequest userDto) {
        User oldUser = userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));
        User newUser = new User(oldUser);
        userMapper.updateModelFromDto(newUser, userDto);
        return userMapper.toDto(userDao.update(newUser));
    }

    public void deleteUser(Integer id) {
        userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));
        userDao.delete(id);
    }

    private boolean isEmailFree(String email) {
        return userDao.findByEmail(email).isEmpty();
    }
}
