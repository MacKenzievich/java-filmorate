package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.time.LocalDate;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService();
    }

    @Test
    void createUser_ValidData_ShouldWork() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        User created = service.create(user);
        assertNotNull(created.getId());
        assertEquals("test@example.com", created.getEmail());
        assertEquals("testlogin", created.getLogin());
        assertEquals("Test User", created.getName());
        assertEquals(LocalDate.of(1990, 5, 20), created.getBirthday());
    }

    @Test
    void createUser_InvalidEmail_ShouldThrow() {
        User user = new User();
        user.setEmail("invalidemail");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> service.create(user));
    }

    @Test
    void createUser_BlankLogin_ShouldSetNameToLogin() {
        User user = new User();
        user.setEmail("test2@example.com");
        user.setLogin("loginname");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = service.create(user);
        assertEquals("loginname", created.getName());
    }

    @Test
    void createUser_BirthdayInFuture_ShouldThrow() {
        User user = new User();
        user.setEmail("test3@example.com");
        user.setLogin("login3");
        user.setBirthday(LocalDate.of(3000, 1, 1));

        assertThrows(ValidationException.class, () -> service.create(user));
    }

    @Test
    void updateUser_ExistingId_ShouldUpdate() {
        User user = new User();
        user.setEmail("user@domain.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = service.create(user);
        Long id = created.getId();

        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setEmail("new@domain.com");
        updateUser.setLogin("newlogin");
        updateUser.setName("New Name");
        updateUser.setBirthday(LocalDate.of(1995, 5, 5));

        User updated = service.update(updateUser);
        assertEquals("new@domain.com", updated.getEmail());
        assertEquals("New Name", updated.getName());
    }

    @Test
    void updateUser_NonExistingId_ShouldThrow() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@domain.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> service.update(user));
    }

    @Test
    void updateUser_WithoutId_ShouldThrow() {
        User user = new User();
        user.setEmail("test@domain.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ConditionsNotMetException.class, () -> service.update(user));
    }

    @Test
    void validate_InvalidLogin_ShouldThrow() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("bad login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> service.create(user));
    }
}