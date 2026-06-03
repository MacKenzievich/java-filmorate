package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
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
    private Set<Long> friendsId = new HashSet<>();

    public void addFriendsId(Long id) {
        friendsId.add(id);
    }

    public void deleteFriendsId(Long id) {
        if (!friendsId.contains(id)) {
            log.warn("User-a с таким id = " + id + " нет в друзьях при удалении из друзей");
            throw new NotFoundException("User-a с таким id = " + id + " нет в друзьях");
        }
        friendsId.remove(id);
    }
}