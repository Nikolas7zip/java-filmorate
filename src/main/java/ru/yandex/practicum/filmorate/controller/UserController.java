package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User newUser) {
        try {
            validate(newUser);
            changeBlankNameToLogin(newUser);
            newUser.setId(++id);
            users.put(id, newUser);
            log.info("Success create new user: {}", newUser);
        } catch (ValidationException ex) {
            log.warn("Fail create user " + newUser + ": " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User updatedUser) {
        try {
            int userId = updatedUser.getId();
            if (users.containsKey(userId)) {
                validate(updatedUser);
                changeBlankNameToLogin(updatedUser);
                users.put(userId, updatedUser);
                log.info("Success update user: {}", updatedUser);
            } else {
                log.warn("Fail update user " + updatedUser + ": Not found user with id " + userId);
                return ResponseEntity.notFound().build();
            }
        } catch (ValidationException ex) {
            log.warn("Fail update user " + updatedUser + ": " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    public static void validate(User user) throws ValidationException {
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            throw new ValidationException("User email " + user.getEmail() + " does not match the pattern");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Blank user login or contains spaces");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday in future: " + user.getBirthday());
        }
    }

    private void changeBlankNameToLogin(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
