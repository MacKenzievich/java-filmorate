package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Long id) {
        if (!userStorage.search(id)) {
            log.warn("user c таким id = " + id + " не найден при получении юзера");
            throw new NotFoundException("user c таким id = " + id + " не найден");
        }
        return userStorage.getUser(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        validate(user);
        return userStorage.add(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Попытка обновить user с пустым полем ID: {}", newUser);
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!userStorage.search(newUser.getId())) {
            log.warn("Попытка обновить user с несуществуещим ID");
            throw new NotFoundException("User с таким id = " + newUser.getId() + " не найден");
        }
        return userStorage.update(newUser);
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
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

    public User addToFriend(Long id, Long friendsId) {
        User firstUser = getUser(id);
        User secondUser = getUser(friendsId);
        firstUser.addFriendsId(friendsId);
        secondUser.addFriendsId(id);
        return firstUser;
    }

    public void deleteFromFriends(Long id, Long friendsId) {
        User firstUser = getUser(id);
        User secondUser = getUser(friendsId);
        firstUser.deleteFriendsId(friendsId);
        secondUser.deleteFriendsId(id);
    }

    public Collection<User> getFriends(Long id) {
        return getUser(id).getFriendsId().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getMutualFriends(Long id, Long otherId) {
        Set<Long> firstUserFriendsId = getUser(id).getFriendsId();
        Set<Long> secondUserFriendsId = getUser(otherId).getFriendsId();
        Set<Long> mutualFriendsId = new HashSet<>(firstUserFriendsId);
        mutualFriendsId.retainAll(secondUserFriendsId);
        return mutualFriendsId.stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }
}



