package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserLoginValidator.class)
@Documented
public @interface UserLogin {
    String message() default "User login ${validatedValue} is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}