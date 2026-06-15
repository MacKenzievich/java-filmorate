package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable("id") int id) {
        return filmService.findGenreById(id);
    }
}