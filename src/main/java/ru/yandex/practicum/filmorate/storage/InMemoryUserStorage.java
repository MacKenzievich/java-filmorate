package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public User add(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public boolean search(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getMutualFriendsFromDB(Long id, Long otherId){
        Set<Long> firstUserFriendsId = getUser(id).getFriendsId();
        Set<Long> secondUserFriendsId = getUser(otherId).getFriendsId();
        Set<Long> mutualFriendsId = new HashSet<>(firstUserFriendsId);
        mutualFriendsId.retainAll(secondUserFriendsId);
        return mutualFriendsId.stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }
}
