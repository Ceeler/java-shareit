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

    private final UserRepository userRepository;

    public UserResponse getUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    public List<UserResponse> getAll() {
        return UserMapper.toDtoList(userRepository.findAll());
    }

    public UserResponse saveUser(UserCreateRequest userDto) {
        User newUser = UserMapper.toModel(userDto);
//        if (!isEmailFree(newUser.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
        User user = userRepository.save(newUser);
        return UserMapper.toDto(user);
    }

    public UserResponse updateUser(Integer id, UserUpdateRequest userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        UserMapper.updateModelFromDto(user, userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(Integer id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.deleteById(id);
    }
}
