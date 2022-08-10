package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotBlank(message = "User email should not be blank or null")
    @Email(message = "User email does not match the pattern")
    private String email;

    @NotBlank(message = "User login should not be blank or null")
    private String login;

    private String name;

    @NotNull(message = "User birthday should not null")
    @PastOrPresent(message = "User birthday should not be in future")
    private LocalDate birthday;
}
