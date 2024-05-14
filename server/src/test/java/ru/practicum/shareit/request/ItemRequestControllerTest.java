package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto requestResponse = ItemRequestDto.builder()
            .id(1)
            .description("test description")
            .created(LocalDateTime.now())
            .items(List.of(ItemForItemRequestDto.builder()
                    .name("test1")
                    .description("test1 description")
                    .available(true)
                    .requestId(1)
                    .build()))
            .build();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    @Test
    void addRequestNoUserHeaderShouldReturn400() throws Exception {
        when(itemRequestService.addItemRequest(any(), anyInt()))
                .thenReturn(requestResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
    }

    @Test
    void addRequestReturnRequestResponse() throws Exception {
        when(itemRequestService.addItemRequest(any(), anyInt()))
                .thenReturn(requestResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestResponse))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestResponse.getDescription())))
                .andExpect(jsonPath("$.items.length()", is(requestResponse.getItems().size())))
                .andExpect(jsonPath("$.items[0].name", is(requestResponse.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(requestResponse.getItems().get(0).getDescription())));
    }

    @Test
    void getOwnRequestsNoUserHeaderShouldReturn400() throws Exception {
        when(itemRequestService.getOwnItemRequest(anyInt()))
                .thenReturn(List.of(requestResponse));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
    }

    @Test
    void getOwnRequestsReturnRequestResponse() throws Exception {
        final int userId = 1;

        when(itemRequestService.getOwnItemRequest(userId))
                .thenReturn(List.of(requestResponse));


        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestResponse.getDescription())))
                .andExpect(jsonPath("$[0].items.length()", is(requestResponse.getItems().size())))
                .andExpect(jsonPath("$[0].items[0].name", is(requestResponse.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(requestResponse.getItems().get(0).getDescription())));
    }

    @Test
    void getAllRequestsNoUserHeaderShouldReturn400() throws Exception {
        when(itemRequestService.getAllItemRequest(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(requestResponse));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
    }

    @Test
    void getAllRequestsWrongPageShouldReturn400() throws Exception {
        when(itemRequestService.getAllItemRequest(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(requestResponse));

        mvc.perform(get("/requests/all?from=0&size=0")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Page must be from>0 and size>1")));
    }

    @Test
    void getAllRequestsReturnRequestResponse() throws Exception {
        final int userId = 1;

        when(itemRequestService.getAllItemRequest(userId, 0, 20))
                .thenReturn(List.of(requestResponse));


        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(requestResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(formatter.format(requestResponse.getCreated()))))
                .andExpect(jsonPath("$[0].items.length()", is(requestResponse.getItems().size())))
                .andExpect(jsonPath("$[0].items[0].name", is(requestResponse.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(requestResponse.getItems().get(0).getDescription())));
    }

    @Test
    void getRequestNoUserHeaderShouldReturn400() throws Exception {
        when(itemRequestService.getItemRequest(anyInt(), anyInt()))
                .thenReturn(requestResponse);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
    }

    @Test
    void getRequestReturnRequestResponse() throws Exception {
        final int userId = 1;
        final int requestId = 1;

        when(itemRequestService.getItemRequest(requestId, userId))
                .thenReturn(requestResponse);


        mvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(requestResponse.getDescription())))
                .andExpect(jsonPath("$.items.length()", is(requestResponse.getItems().size())))
                .andExpect(jsonPath("$.items[0].name", is(requestResponse.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(requestResponse.getItems().get(0).getDescription())));
    }
}
