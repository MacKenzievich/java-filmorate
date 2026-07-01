package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;


    public Film create(Film film) {
        validateMpa(film);
        validateGenres(film);
        return filmStorage.create(film);
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null || !Mpa.containsId(film.getMpa().getId())) {
            throw new MpaNotFoundException("Неверный MPA");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || !film.validateGenres()) {
            throw new GenreNotFoundException("Неверный жанр");
        }
    }

    public Film update(Film film) {
        if (filmStorage.findFilmById(film.getId()).isEmpty()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
        return filmStorage.update(film);
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        genreStorage.findAllGenresByFilm(films);
        return films;
    }

    public Film findFilmById(int id) {
        Film film = filmStorage.findFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден."));
        genreStorage.findAllGenresByFilm(List.of(film));
        return film;
    }

    public void addLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        likeStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        likeStorage.removeLike(id, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.findPopular(count);
        genreStorage.findAllGenresByFilm(films);
        return films;
    }

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(int id) {
        return mpaStorage.findMpaById(id).orElseThrow(() -> new MpaNotFoundException("Рейтинг MPA не найден."));
    }

    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return genreStorage.findGenreById(id).orElseThrow(() -> new GenreNotFoundException("Жанр не найден."));
    }

    public List<Film> findSortFilmsByDirector(int id, String sortBy) {
        if (directorStorage.findDirectorById(id).isEmpty()) {
            throw new DirectorNotFoundException("Режиссер не найден.");
        }
        List<Film> films = new ArrayList<>();
        if (sortBy.equals("year")) {
            films = filmStorage.findDirectorsFilmsByYear(id);
        } else if (sortBy.equals("likes")) {
            films = filmStorage.findDirectorsFilmsByLikes(id);
        }
        genreStorage.findAllGenresByFilm(films);
        directorStorage.findAllDirectorsByFilm(films);
        return films;
    }

    public List<Film> findFilmsByNameByDirector(String query, String by) {
        List<Film> films = filmStorage.findFilmsByNameByDirector(query, by);
        genreStorage.findAllGenresByFilm(films);
        directorStorage.findAllDirectorsByFilm(films);
        return films;
    }

}