package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);
    private static final Map<Long, Film> films = new HashMap<>();


    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Creating film {}", film);
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Попытка обновить фильм с пустым полем ID: {}", newFilm);
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновить фильм с несущестсвуещим ID");
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        validate(newFilm);  //Почему нам это не нужно? если при обновлении мы можем это поле сделать невалидное
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            log.warn("Попытка добавить фильм с неверной датой релиза: {}", film);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

    }

    private long getNextId() {
        long currentMaxId = FilmService.films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
