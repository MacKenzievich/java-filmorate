package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> getAllDirectors() {
        return service.findAllDirectors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable int id) {
        return service.findDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@Valid @RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirectorById(@PathVariable int id) {
        service.deleteDirectorById(id);
    }

}
