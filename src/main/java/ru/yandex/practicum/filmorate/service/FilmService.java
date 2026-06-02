package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Попытка обновить фильм с пустым полем ID: {}", newFilm);
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!filmStorage.search(newFilm.getId())) {
            log.warn("Попытка обновить фильм с несущестсвуещим ID");
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        return filmStorage.update(newFilm);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            log.warn("Попытка добавить фильм с неверной датой релиза: {}", film);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

    }


}
