package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserService(@Qualifier("UserMemoryDao") UserDao dao,
                       UserMapper userMapper) {
        this.userDao = dao;
        this.userMapper = userMapper;
    }

    public UserDto getUser(Integer id) {
        User user = userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getAll() {
        return userMapper.toDtoList(userDao.getAll());
    }

    public UserDto saveUser(UserDto userDto) {
        User newUser = userMapper.toModel(userDto);
        if (!isEmailFree(newUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = userDao.save(newUser);
        return userMapper.toDto(user);
    }

    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getEmail() != null && (!userDto.getEmail().equals(user.getEmail())) &&
                !isEmailFree(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        userMapper.updateModelFromDto(user, userDto);
        return userMapper.toDto(userDao.update(user));
    }

    public void deleteUser(Integer id) {
        userDao.get(id).orElseThrow(() -> new NotFoundException("User not found"));
        userDao.delete(id);
    }

    private boolean isEmailFree(String email) {
        return userDao.findByEmail(email).isEmpty();
    }
}
