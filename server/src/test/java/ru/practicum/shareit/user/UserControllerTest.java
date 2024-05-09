package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserResponse userResponse = UserResponse.builder()
            .id(1)
            .name("Test")
            .email("test@mail.ru")
            .build();

    @Test
    void saveNewUserReturnUserResponse() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(userResponse);


        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    void getUserReturnUserResponse() throws Exception {
        when(userService.getUser(anyInt()))
                .thenReturn(userResponse);


        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    void getAllUserReturnUserResponse() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userResponse));


        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userResponse.getName())))
                .andExpect(jsonPath("$[0].email", is(userResponse.getEmail())));
    }

//    @Test
//    void updateUserAllNullReturn400() throws Exception {
//        when(userService.updateUser(anyInt(), any()))
//                .thenReturn(userResponse);
//
//        UserUpdateRequest update = UserUpdateRequest.builder()
//                .email(null)
//                .build();
//
//        mvc.perform(patch("/users/1")
//                        .content(mapper.writeValueAsString(update))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void updateUserReturnUserResponse() throws Exception {
        when(userService.updateUser(anyInt(), any()))
                .thenReturn(userResponse);

        UserUpdateRequest update = UserUpdateRequest.builder()
                .email("newEmail@test.ru")
                .build();

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(update))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
