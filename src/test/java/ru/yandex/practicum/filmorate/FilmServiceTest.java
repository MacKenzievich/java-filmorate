package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    private Film validFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Matrix");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(120L);
        return film;
    }

    @Test
    void shouldPassValidation_whenFilmValid() {
        Film film = validFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenNameIsNull() {
        Film film = validFilm();
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFail_whenNameIsBlank() {
        Film film = validFilm();
        film.setName("   ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFail_whenDescriptionLongerThan200() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFail_whenReleaseDateIsNull() {
        Film film = validFilm();
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFail_whenDurationLessThan1() {
        Film film = validFilm();
        film.setDuration(0L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }
}
