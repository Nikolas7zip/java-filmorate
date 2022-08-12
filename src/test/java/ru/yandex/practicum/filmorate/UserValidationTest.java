package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setEmail("tester@mail.ru");
        user.setLogin("Tester");
        user.setBirthday(LocalDate.of(1992, 5, 13));
    }

    @Test
    public void shouldBeCorrectUser() {
        assertEquals(0, validator.validate(user).size());
    }

    @Test
    public void shouldCheckNullEmail() {
        user.setEmail(null);
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckEmptyEmail() {
        user.setEmail("");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckWrongEmail() {
        user.setEmail("tester-mail.ru");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckEmailWithoutPrefix() {
        user.setEmail("@mail.ru");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckEmptyLogin() {
        user.setLogin("");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckLoginWithSpaces() {
        user.setLogin("Tester Cool");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    public void shouldCheckFutureBirthday() {
        user.setBirthday(LocalDate.of(2023, 1, 1));
        assertEquals(1, validator.validate(user).size());
    }
}
