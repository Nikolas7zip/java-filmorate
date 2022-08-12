package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmDateValidator implements ConstraintValidator<FilmDate, LocalDate> {
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && !value.isBefore(cinemaBirthday);
    }
}
