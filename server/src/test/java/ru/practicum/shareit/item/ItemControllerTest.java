package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.comment.dto.response.CommentResponse;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemGetResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    private final CommentResponse commentResponse = CommentResponse.builder()
            .id(1)
            .text("test text")
            .authorName("Test")
            .created(LocalDateTime.now())
            .build();

    private final ItemGetResponse itemGetResponse = ItemGetResponse.builder()
            .id(1)
            .name("Test")
            .description("Test description")
            .available(true)
            .nextBooking(BookingBookerDto.builder()
                    .id(1)
                    .bookerId(1)
                    .name("Test 2")
                    .build())
            .build();
    private final ItemResponse itemResponse = ItemResponse.builder()
            .id(1)
            .name("Test")
            .description("Test description")
            .available(true)
            .build();

    @Test
    void getItemNotFoundReturn404() throws Exception {
        final int userId = 1;

        when(itemService.getItem(2, userId))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item not found")));
    }

    @Test
    void getItemReturnItemResponse() throws Exception {
        final int userId = 1;

        when(itemService.getItem(2, userId))
                .thenReturn(itemGetResponse);


        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemGetResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemGetResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemGetResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemGetResponse.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemGetResponse.getNextBooking().getId())));
    }

//    @Test
//    void getAllItemWrongPageShouldReturn400AndMessage() throws Exception {
//        final int userId = 1;
//
//        when(itemService.getAll(userId, 0, 20))
//                .thenReturn(List.of(itemGetResponse));
//
//
//        mvc.perform(get("/items?from=0&size=0")
//                        .header("X-Sharer-User-Id", userId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Page must be from>0 and size>1")));
//    }

//    @Test
//    void getAllItemNoHeaderShouldReturn400() throws Exception {
//        final int userId = 1;
//
//        when(itemService.getAll(userId, 0, 20))
//                .thenReturn(List.of(itemGetResponse));
//
//        mvc.perform(get("/items")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void getAllItemShouldReturnItemResponse() throws Exception {
        final int userId = 1;

        when(itemService.getAll(userId, 0, 20))
                .thenReturn(List.of(itemGetResponse));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemGetResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemGetResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemGetResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemGetResponse.getAvailable())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemGetResponse.getNextBooking().getId())));
    }

//    @Test
//    void saveItemNoHeaderShouldReturn400() throws Exception {
//        final int userId = 1;
//
//        when(itemService.getAll(userId, 0, 20))
//                .thenReturn(List.of(itemGetResponse));
//
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemGetResponse))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

//    @Test
//    void saveItemWrongValidation400() throws Exception {
//        final int userId = 1;
//
//        ItemCreateRequest createRequest = ItemCreateRequest.builder()
//                .name("")
//                .description("")
//                .build();
//
//        when(itemService.saveItem(any(), anyInt()))
//                .thenReturn(itemResponse);
//
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(createRequest))
//                        .header("X-Sharer-User-Id", userId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.length()", is(3)));
//    }


    @Test
    void saveItemInnerErrorReturn500() throws Exception {
        final int userId = 1;

        when(itemService.saveItem(any(), anyInt()))
                .thenThrow(new RuntimeException("Some inner error"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemResponse))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Some inner error")));
    }

    @Test
    void saveItemShouldReturnItemResponse() throws Exception {
        final int userId = 1;

        when(itemService.saveItem(any(), anyInt()))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemGetResponse))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

//    @Test
//    void updateItemNoHeaderShouldReturn400() throws Exception {
//        final int userId = 1;
//
//        when(itemService.updateItem(userId, new ItemUpdateRequest(), 1))
//                .thenReturn(itemResponse);
//
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(itemGetResponse))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void updateItemByNotOwnerShouldReturn404() throws Exception {
        when(itemService.updateItem(anyInt(), any(), anyInt()))
                .thenThrow(new NotAuthorizedException("You can't change this item"));

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemGetResponse))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("You can't change this item")));
    }

//    @Test
//    void updateItemAllNullReturn400() throws Exception {
//        when(itemService.updateItem(anyInt(), any(), anyInt()))
//                .thenReturn(itemResponse);
//
//        ItemUpdateRequest update = ItemUpdateRequest.builder()
//                .name(null)
//                .build();
//
//        mvc.perform(patch("/items/1")
//                        .content(mapper.writeValueAsString(update))
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void updateItemShouldReturnItemResponse() throws Exception {
        when(itemService.updateItem(anyInt(), any(), anyInt()))
                .thenReturn(itemResponse);

        ItemUpdateRequest update = ItemUpdateRequest.builder()
                .name("newName")
                .build();

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(update))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

//    @Test
//    void deleteItemNoHeaderShouldReturn400() throws Exception {
//        mvc.perform(delete("/items/1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void deleteItemShouldReturn200() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

//    @Test
//    void searchItemWrongPageShouldReturn400AndMessage() throws Exception {
//        final int userId = 1;
//
//        when(itemService.getAll(userId, 0, 20))
//                .thenReturn(List.of(itemGetResponse));
//
//        mvc.perform(get("/items/search?text=test&from=0&size=0")
//                        .header("X-Sharer-User-Id", userId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Page must be from>0 and size>1")));
//    }

    @Test
    void searchItemShouldReturnItemResponse() throws Exception {
        when(itemService.findByText("test", 0, 20))
                .thenReturn(List.of(itemResponse));


        mvc.perform(get("/items/search?text=test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponse.getAvailable())));
    }

//    @Test
//    void addCommentNoHeaderShouldReturn400() throws Exception {
//        final int userId = 1;
//
//        when(itemService.getAll(userId, 0, 20))
//                .thenReturn(List.of(itemGetResponse));
//
//        mvc.perform(post("/items/1/comment")
//                        .content(mapper.writeValueAsString(commentResponse))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void addCommentShouldReturnCommentResponse() throws Exception {
        final int userId = 1;

        when(itemService.addComment(any(), anyInt(), anyInt()))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentResponse))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())));
    }
}
