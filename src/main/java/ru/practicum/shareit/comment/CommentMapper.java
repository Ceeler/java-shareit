package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.comment.dto.response.CommentResponse;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toModel(CommentRequest dto, User author, Item item) {
        return Comment.builder()
                .text(dto.getText())
                .author(author)
                .item(item)
                .build();
    }

    public static CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreatedAt())
                .build();
    }

    public static List<CommentResponse> toDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

}
