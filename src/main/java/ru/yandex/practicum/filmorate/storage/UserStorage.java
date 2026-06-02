package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User add(User user);

    User update(User user);

    boolean search(Long id);

    Collection<User> getUsers();

    User getUser(Long id);
}
