package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilmDateValidator.class)
@Documented
public @interface FilmDate {
    String message() default "Film date ${validatedValue} is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
