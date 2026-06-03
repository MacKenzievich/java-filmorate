package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<User> findAll() {
        return service.getUsers();
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(user));
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User newUser) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(newUser));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addToFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getMutualFriends(id, otherId);
    }
}