package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    private final BookingResponse bookingResponse = BookingResponse.builder()
            .id(1)
            .start(LocalDateTime.now().minusHours(1))
            .end(LocalDateTime.now().plusHours(1))
            .item(ItemNameDto.builder()
                    .id(1)
                    .name("testItem")
                    .build())
            .booker(UserResponse.builder()
                    .id(1)
                    .name("Test")
                    .email("test@mail.ru")
                    .build())
            .status(Status.APPROVED)
            .build();

//    @Test
//    void getBookNoUserHeaderReturn400() throws Exception {
//        final int userId = 1;
//        when(bookingService.getBooking(1, userId))
//                .thenReturn(bookingResponse);
//
//        mvc.perform(get("/bookings/1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void getBookReturnBookingResponse() throws Exception {
        final int userId = 1;
        when(bookingService.getBooking(1, userId))
                .thenReturn(bookingResponse);


        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingResponse.getStart()))))
                .andExpect(jsonPath("$.end", is(formatter.format(bookingResponse.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId())));
    }

//    @Test
//    void bookItemNoUserHeaderReturn400() throws Exception {
//        when(bookingService.bookItem(anyInt(), any()))
//                .thenReturn(bookingResponse);
//
//        BookingCreateRequest request = BookingCreateRequest.builder()
//                .itemId(1)
//                .start(LocalDateTime.now().plusHours(1))
//                .end(LocalDateTime.now().plusHours(2))
//                .build();
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(request))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

//    @Test
//    void bookItemStartAfterAndReturn400() throws Exception {
//        when(bookingService.bookItem(anyInt(), any()))
//                .thenReturn(bookingResponse);
//
//        BookingCreateRequest request = BookingCreateRequest.builder()
//                .itemId(1)
//                .start(LocalDateTime.now().plusHours(2))
//                .end(LocalDateTime.now().plusHours(1))
//                .build();
//
//        mvc.perform(post("/bookings")
//                        .content(mapper.writeValueAsString(request))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void bookItemReturnBookingResponse() throws Exception {
        when(bookingService.bookItem(anyInt(), any()))
                .thenReturn(bookingResponse);

        BookingCreateRequest request = BookingCreateRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingResponse.getStart()))))
                .andExpect(jsonPath("$.end", is(formatter.format(bookingResponse.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId())));
    }

//    @Test
//    void getUserBookingsWrongPageReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings?text=test&from=0&size=0")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .header("X-Sharer-User-Id", 1)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Page must be from>0 and size>1")));
//    }

//    @Test
//    void getUserBookingsWrongStateReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings?state=ALLIN")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .header("X-Sharer-User-Id", 1)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Unknown state: ALLIN")));
//    }

//    @Test
//    void getUserBookingsNoUserHeaderReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void getUserBookingsReturnBookingResponse() throws Exception {
        when(bookingService.getUserBookings(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(formatter.format(bookingResponse.getStart()))))
                .andExpect(jsonPath("$[0].end", is(formatter.format(bookingResponse.getEnd()))))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId())));
    }

//    @Test
//    void approveBookNoRequestReturn400() throws Exception {
//        when(bookingService.approveBookingStatus(anyInt(), anyInt(), anyBoolean()))
//                .thenReturn(bookingResponse);
//
//        mvc.perform(patch("/bookings/1")
//                        .header("X-Sharer-User-Id", 1)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Approved must be specified")));
//    }

//    @Test
//    void approveBookNoUserHeaderReturn400() throws Exception {
//        when(bookingService.approveBookingStatus(anyInt(), anyInt(), anyBoolean()))
//                .thenReturn(bookingResponse);
//
//        mvc.perform(patch("/bookings/1?approved=true")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

    @Test
    void approveBookReturnBookResponse() throws Exception {
        when(bookingService.approveBookingStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingResponse.getStart()))))
                .andExpect(jsonPath("$.end", is(formatter.format(bookingResponse.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId())));
    }

//    @Test
//    void getUserItemsBookingsWrongPageReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings/owner?text=test&from=0&size=0")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .header("X-Sharer-User-Id", 1)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Page must be from>0 and size>1")));
//    }

//    @Test
//    void getUserNoUserHeaderReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings/owner")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Header X-Sharer-User-Id requested")));
//    }

//    @Test
//    void getUserItemsBookingsWrongStateReturn400() throws Exception {
//        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingResponse));
//
//        mvc.perform(get("/bookings/owner?state=ALLIN")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .header("X-Sharer-User-Id", 1)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error", is("Unknown state: ALLIN")));
//    }

    @Test
    void getUserItemsBookings() throws Exception {
        when(bookingService.getUserItemsBookings(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(formatter.format(bookingResponse.getStart()))))
                .andExpect(jsonPath("$[0].end", is(formatter.format(bookingResponse.getEnd()))))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId())));
    }
}
