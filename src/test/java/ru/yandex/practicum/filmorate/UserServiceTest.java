package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void validUser_hasNoViolations() {
        User u = validUser();

        Set<ConstraintViolation<User>> violations = validator.validate(u);

        assertTrue(violations.isEmpty());
    }

    @Test
    void email_blank_violation() {
        User u = validUser();
        u.setEmail("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(u);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void email_notEmail_violation() {
        User u = validUser();
        u.setEmail("not-an-email");

        Set<ConstraintViolation<User>> violations = validator.validate(u);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void login_blank_violation() {
        User u = validUser();
        u.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(u);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void birthday_null_violation() {
        User u = validUser();
        u.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(u);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    private User validUser() {
        User u = new User();
        u.setEmail("test@mail.com");
        u.setLogin("login");
        u.setName("Name");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return u;
    }
}
