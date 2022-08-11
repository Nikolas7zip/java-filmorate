package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) throws BadRequestException {
        throwIfLoginNotValid(newUser);

        changeBlankNameToLogin(newUser);
        newUser.setId(++id);
        users.put(id, newUser);
        log.info("Success create {}", newUser);

        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) throws ResourceNotFoundException, BadRequestException {
        int userId = updatedUser.getId();
        if (!users.containsKey(userId)) {
            String warningMessage = "Not found user with id " + userId;
            log.warn(warningMessage);
            throw new ResourceNotFoundException(warningMessage);
        }

        throwIfLoginNotValid(updatedUser);

        changeBlankNameToLogin(updatedUser);
        users.put(userId, updatedUser);
        log.info("Success update {}", updatedUser);

        return updatedUser;
    }

    public void throwIfLoginNotValid(User user) throws BadRequestException {
        if (user.getLogin().contains(" ")) {
            String warningMessage = "User login contains spaces: " + user.getLogin();
            log.warn(warningMessage);
            throw new BadRequestException(warningMessage);
        }
    }

    private void changeBlankNameToLogin(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
