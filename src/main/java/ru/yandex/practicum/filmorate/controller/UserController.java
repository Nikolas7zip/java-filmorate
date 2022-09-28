package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        return userService.createUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        return userService.updateUser(updatedUser);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserToFriends(@PathVariable long id,
                                 @PathVariable long friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeUserFromFriends(@PathVariable long id,
                                      @PathVariable long friendId) {
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriendsBetweenTwoUsers(@PathVariable long id,
                                                       @PathVariable long otherId) {
        return userService.findCommonFriendsBetweenTwoUsers(id, otherId);
    }
}
