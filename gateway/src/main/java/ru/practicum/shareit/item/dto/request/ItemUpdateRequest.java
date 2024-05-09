package ru.practicum.shareit.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemUpdateValid;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ItemUpdateValid
public class ItemUpdateRequest {

    private String name;

    private String description;

    private Boolean available;

}
