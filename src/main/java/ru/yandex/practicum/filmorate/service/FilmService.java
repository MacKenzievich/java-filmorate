package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(Long id) {
        if (!filmStorage.search(id)) {
            log.warn("Film c указанным id = " + id + " не найден при получении фильма");
            throw new NotFoundException("Film c указанным id = " + id + " не найден");
        }
        return filmStorage.getFilm(id);
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
            throw new NotFoundException("Фильм с таким id = " + newFilm.getId() + " не найден");
        }
        return filmStorage.update(newFilm);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            log.warn("Попытка добавить фильм с неверной датой релиза: {}", film);
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

    }

    public void addLike(Long id, Long userId) {
        if (!userStorage.search(userId)) {
            log.warn("User c таким id " + id + " не найден при добавлении лайка");
            throw new NotFoundException("User c таким id " + id + " не найден");
        }
        getFilm(id).addUserLike(userId);
    }

    public void deleteLike(Long id, Long userId) {
        getFilm(id).deleteUserLike(userId);
    }

    public Collection<Film> getPopularFilms(Long count) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikedByUserIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
