package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage storage;

    private int insertedUserId;
    private int userCounter = 0;

    @BeforeEach
    void setUp() {
        User user = createTestUser();
        User savedUser = storage.create(user);
        insertedUserId = savedUser.getId();
    }


    public String generateUniqueLogin() {
        return "testlogin_" + System.currentTimeMillis() + "_" + (userCounter++);
    }

    private User createTestUser() {
        return User.builder()
                .email("testuser@example.com")
                .login(generateUniqueLogin())
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void testCreateUser() {
        User newUser = User.builder()
                .email("newuser@example.com")
                .login("newlogin")
                .name("New User")
                .birthday(LocalDate.of(1985, 5, 20))
                .build();

        User createdUser = storage.create(newUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser)
                .hasFieldOrPropertyWithValue("email", "newuser@example.com")
                .hasFieldOrPropertyWithValue("login", "newlogin")
                .hasFieldOrPropertyWithValue("name", "New User")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1985, 5, 20));
    }

    @Test
    void testUpdateUser() {
        Optional<User> userOpt = storage.findUserById(insertedUserId);
        assertThat(userOpt).isPresent();
        User user = userOpt.get();
        user.setName("Updated Name");
        user.setEmail("updatedemail@example.com");
        user.setBirthday(LocalDate.of(1999, 12, 31));
        storage.update(user);
        Optional<User> updatedUserOpt = storage.findUserById(insertedUserId);
        assertThat(updatedUserOpt).isPresent();
        User updatedUser = updatedUserOpt.get();
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated Name");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "updatedemail@example.com");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 12, 31));
    }

}