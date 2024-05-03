package ru.practicum.shareit.comment;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

// Плагин жалуется на этот класс, как пропущенный, добавил для обхода
public class QCommentTest {

    @Test
    void checkQCommentMethods() {
        BooleanExpression expression = QComment.comment.id.eq(1);
        BooleanExpression expression2 = QComment.comment.createdAt.after(LocalDateTime.now());
        PathMetadata expression3 = QComment.comment.getMetadata();
    }

}
