package ru.practicum.shareit.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserUpdateValid;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@UserUpdateValid
public class UserUpdateRequest {

    private String name;

    private String email;
}
