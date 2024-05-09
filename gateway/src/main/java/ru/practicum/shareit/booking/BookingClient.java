package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> bookItem(Integer userId, BookingCreateRequest dto) {
        return post("", userId.longValue(), dto);
    }

    public ResponseEntity<Object> approveBookingStatus(Integer id, Integer userId, boolean isApproved) {
        Map<String, Object> parameters = Map.of(
                "approved", isApproved
        );
        return patch("/" + id + "?approved={approved}", userId.longValue(), parameters, null);
    }

    public ResponseEntity<Object> getBooking(Integer id, Integer userId) {
        return get("/" + id, userId.longValue());
    }

    public ResponseEntity<Object> getUserBookings(Integer userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId.longValue(), parameters);
    }

    public ResponseEntity<Object> getUserItemsBookings(Integer userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId.longValue(), parameters);
    }

}
