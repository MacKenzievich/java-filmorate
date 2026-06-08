package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Long, Film> films = new HashMap<>();
    private long currentId = 1;

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public Film add(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean search(Long id) {
        return films.containsKey(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilmsFromDB(Long count){
       return getFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikedByUserIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
