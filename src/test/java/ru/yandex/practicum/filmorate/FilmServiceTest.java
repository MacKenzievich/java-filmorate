package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

class FilmServiceTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService();
    }

    @Test
    void createFilm_ValidData_ShouldWork() {
        Film film = new Film();
        film.setName("Тест");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90L);

        Film created = service.create(film);
        assertNotNull(created.getId());
        assertEquals("Тест", created.getName());
    }

    @Test
    void validate_FilmWithEmptyName_ShouldThrow() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            service.create(film);
        });
        assertEquals("название не может быть пустым;", exception.getMessage());
    }

    @Test
    void validate_DescriptionTooLong_ShouldThrow() {
        Film film = new Film();
        film.setName("Тест");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(30L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            service.create(film);
        });
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void validate_ReleaseDateBefore1895_ShouldThrow() {
        Film film = new Film();
        film.setName("Тест");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(60L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            service.create(film);
        });
        assertEquals("дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void validate_NonPositiveDuration_ShouldThrow() {
        Film film = new Film();
        film.setName("Тест");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0L);

        Exception exception = assertThrows(ValidationException.class, () -> {
            service.create(film);
        });
        assertEquals("продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    @Test
    void update_NonExistingId_ShouldThrow() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Обновление");
        film.setDescription("Обновленное описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.update(film);
        });
    }

    @Test
    void update_WithoutId_ShouldThrow() {
        Film film = new Film();
        film.setName("Обновление");
        film.setDescription("Обновленное описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
            service.update(film);
        });
        assertEquals("Id должен быть указан", exception.getMessage());
    }
}