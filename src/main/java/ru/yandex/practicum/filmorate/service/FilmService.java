package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class FilmService {
    private final static int MAX_LENGTH_DESCRIPTION = 200;
    private final static LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);
    private final static Map<Long, Film> films = new HashMap<>();
    private final static Logger log = (Logger) LoggerFactory.getLogger(FilmService.class);

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film addFilm(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Creating film {}", film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Попытка обновить фильм с пустым полем ID: {}", newFilm);
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        validate(newFilm);
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновить фильм с несущестсвуещим ID");
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка добавить фильм с пустым названием: {}", film);
            throw new ValidationException("название не может быть пустым;");
        }
        if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            log.warn("Попытка добавить фильм с максимальной длиной описания более 200 символов: {}", film);
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            log.warn("Попытка добавить фильм с неверной датой релиза: {}", film);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Попытка добавить фильм с отрицательной продолжительностью: {}", film);
            throw new ValidationException("продолжительность фильма должна быть положительным числом.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
