package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;



@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return service.getFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return service.update(newFilm);
    }



}
