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
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
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
            throw new NotFoundException("User с таким ID не найден");
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

    public void addToFriend(Long id, Long friendsId){ // проверка под каждого
        if(!userStorage.search(id) && !userStorage.search(friendsId)){
            throw new NotFoundException("User c таким id не найден");
        }
        User firstUser = userStorage.getUser(id);
        User secondUser = userStorage.getUser(friendsId);
        firstUser.addToFriends(friendsId);
        secondUser.addToFriends(id);
    }
}


