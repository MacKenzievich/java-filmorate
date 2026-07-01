package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAllFilms();

    List<Film> findPopular(int count, Integer genreId, Integer year);

    List<Film> findRecommendations(int userId);

    Optional<Film> findFilmById(int id);

    List<Film> findDirectorsFilmsByYear(int id);

    List<Film> findDirectorsFilmsByLikes(int id);

    void deleteFilm(int id);

    List<Film> findCommonFilms(int userId, int friendId);

    List<Film> findFilmsByNameByDirector(String query, String by);
}