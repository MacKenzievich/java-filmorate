package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;


    @GetMapping
    public Collection<Film> findAll() {
        return service.getFilms();
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(film));
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film newFilm) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(newFilm));
    }

    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilm(@PathVariable Long id) {
        return service.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Long count) {
        return service.getPopularFilms(count);
    }
}
