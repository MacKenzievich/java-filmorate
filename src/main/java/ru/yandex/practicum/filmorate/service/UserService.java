package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final static Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User create(User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Creating user {}", user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Попытка обновить user с пустым полем ID: {}", newUser);
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        validate(newUser);
        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновить user с несуществуещим ID");
            throw new NotFoundException("User с таким ID не найден");
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private void validate(User user) {
        if (!user.getEmail().isEmpty() && !user.getEmail().contains("@")) {
            log.warn("Попытка добавить user с некорректным email: {}", user);
            throw new ValidationException("некорректный email;");
        }
        if ((user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" "))) {
            log.warn("Попытка добавить user с некорректным login: {}", user);
            throw new ValidationException("некорректный login;");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавить user с некорректным birthday: {}", user);
            throw new ValidationException("некорректный birthday;");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}


