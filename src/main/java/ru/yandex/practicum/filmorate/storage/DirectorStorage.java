package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAllDirectors();

    Optional<Director> findDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(int id);

    void findAllDirectorsByFilm(List<Film> films);
}
