package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final EventStorage eventStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addFriend(int id, int friendId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        if (id < 0 || friendId < 0) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        friendStorage.addFriend(id, friendId);

        eventStorage.createEvent(
                new Event(
                        null,
                        System.currentTimeMillis(),
                        id,
                        EventType.FRIEND,
                        Operation.ADD,
                        friendId
                )
        );

    }

    public List<User> getFriends(int id) {
        if (userStorage.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return friendStorage.findAllFriends(id);
    }

    public List<User> getMutualFriends(int id, int otherId) {
        return friendStorage.findCommonFriends(id, otherId);
    }

    public void deleteFriend(int id, int friendId) {

        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }

        friendStorage.removeFriend(id, friendId);

        eventStorage.createEvent(
                new Event(
                        null,
                        System.currentTimeMillis(),
                        id,
                        EventType.FRIEND,
                        Operation.REMOVE,
                        friendId
                )
        );
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public List<Event> getFeed(int id) {
        userStorage.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        return eventStorage.findByUserId(id);
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

