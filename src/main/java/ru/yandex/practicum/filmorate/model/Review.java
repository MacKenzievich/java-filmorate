package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private Integer reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым.")
    private String content;

    @NotNull(message = "Необходимо указать тип отзыва.")
    private Boolean isPositive;

    @NotNull(message = "Необходимо указать пользователя.")
    private Integer userId;

    @NotNull(message = "Необходимо указать фильм.")
    private Integer filmId;

    @Builder.Default
    private Integer useful = 0;
}
