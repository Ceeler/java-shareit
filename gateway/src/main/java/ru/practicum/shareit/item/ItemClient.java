package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(Integer id, Integer userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAll(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId.longValue(), parameters);
    }

    public ResponseEntity<Object> saveItem(ItemCreateRequest itemDto, Integer userId) {
        return post("", userId.longValue(), itemDto);
    }

    public ResponseEntity<Object> updateItem(Integer id, ItemUpdateRequest itemDto, Integer userId) {
        return patch("/" + id, userId.longValue(), itemDto);
    }

    public ResponseEntity<Object> deleteItem(Integer id, Integer userId) {
        return delete("/" + id, userId.longValue());
    }

    public ResponseEntity<Object> findByText(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(CommentRequest dto, Integer itemId, Integer userId) {
        return post("/" + itemId + "/comment", userId.longValue(), dto);
    }
}
