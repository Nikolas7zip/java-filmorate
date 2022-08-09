package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    @Test
    public void shouldNotThrowExceptionOnCorrectUser() {
        User user = new User("tester@mail.ru", "Tester", LocalDate.of(1992, 5, 13));

        assertDoesNotThrow(() -> {
            UserController.validate(user);
        });
    }

    @Test
    public void shouldThrowNullPointerExceptionForNullEmail() {
        final NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> {
                    User user = new User(null, "Tester", LocalDate.of(1992, 5, 13));
                });
    }

    @Test
    public void shouldThrowValidationExceptionForEmptyEmail() {
        User user = new User("", "Tester", LocalDate.of(1992, 5, 13));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForWrongEmail() {
        User user = new User("tester-mail.ru", "Tester", LocalDate.of(1992, 5, 13));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForEmailWithoutPrefix() {
        User user = new User("@mail.ru", "Tester", LocalDate.of(1992, 5, 13));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForEmptyLogin() {
        User user = new User("tester@mail.ru", "", LocalDate.of(1992, 5, 13));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForLoginWithSpaces() {
        User user = new User("tester@mail.ru", "Tester Cool", LocalDate.of(1992, 5, 13));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForFutureBirthday() {
        User user = new User("tester@mail.ru", "Tester", LocalDate.of(2023, 1, 1));

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    UserController.validate(user);
                });
    }
}
