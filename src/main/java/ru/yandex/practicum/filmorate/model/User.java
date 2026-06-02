package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public void addToFriends(Long id) {
        friends.add(id);
    }

    public void deleteFromFriends(Long id) {
        if (!friends.contains(id)) {
            throw new NotFoundException("User-a с таким id нет в друзьях");
        }
        friends.remove(id);
    }

    public Collection<Long> getFriends() {
        return friends;
    }
}