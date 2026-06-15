package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;

    public static final Genre COMEDY = new Genre(1, "Комедия");
    public static final Genre DRAMA = new Genre(2, "Драма");
    public static final Genre ANIMATION = new Genre(3, "Мультфильм");
    public static final Genre THRILLER = new Genre(4, "Триллер");
    public static final Genre DOCUMENTARY = new Genre(5, "Документальный");
    public static final Genre ACTION = new Genre(6, "Боевик");

    public static boolean existsById(int id) {
        return id == COMEDY.id
                || id == DRAMA.id
                || id == ANIMATION.id
                || id == THRILLER.id
                || id == DOCUMENTARY.id
                || id == ACTION.id;
    }
}