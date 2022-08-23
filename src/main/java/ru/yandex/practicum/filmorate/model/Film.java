package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.validation.FilmDate;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Film name should not be blank or null")
    private String name;

    @NotBlank(message = "Film description should not be blank or null")
    @Size(max = 200, message = "Max size of description should be 200 chars")
    private String description;

    @NotNull(message = "Film release date should not be null")
    @FilmDate
    private LocalDate releaseDate;

    @Positive(message = "Film duration should be positive")
    private int duration;
}
