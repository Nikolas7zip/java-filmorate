package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(int userId) throws ResourceNotFoundException {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Not found user with id " + userId);
        }

        return user;
    }

    public User createUser(User user) {
        changeBlankNameToLogin(user);
        int userId = userStorage.add(user);
        User userFromStorage = getUserById(userId);
        log.info("Success create {}", userFromStorage);

        return userFromStorage;
    }

    public User updateUser(User user) throws ResourceNotFoundException {
        throwIfUserNotFound(user.getId());      // check user is existed
        changeBlankNameToLogin(user);
        userStorage.update(user);
        User userFromStorage = getUserById(user.getId());
        log.info("Success update {}", userFromStorage);

        return userFromStorage;
    }

    public List<User> getFriends(int userId) throws ResourceNotFoundException {
        throwIfUserNotFound(userId);

        return userStorage.getFriends(userId);
    }

    public void addToFriends(int userId, int friendId)
            throws ResourceNotFoundException, BadRequestException {
        throwIfUserIdsAreEqual(userId, friendId);
        throwIfUserNotFound(userId);
        throwIfUserNotFound(friendId);
        boolean isFriendAdded = userStorage.addToFriends(userId, friendId);
        if (isFriendAdded) {
            log.info("Added user {} as a friend to user {}", friendId, userId);
        } else {
            throw new BadRequestException("Can't add user " + friendId + " as a friend to user " + userId);
        }
    }

    public void removeFromFriends(int userId, int friendId)
            throws ResourceNotFoundException, BadRequestException {
        throwIfUserIdsAreEqual(userId, friendId);
        throwIfUserNotFound(userId);
        throwIfUserNotFound(friendId);
        boolean isFriendRemoved = userStorage.removeFromFriends(userId, friendId);
        if (isFriendRemoved) {
            log.info("Removed user {} from friends of user {}", friendId, userId);
        } else {
            throw new BadRequestException("Can't remove user " + friendId + " from friends user " + userId);
        }
    }

    public List<User> findCommonFriendsBetweenTwoUsers(int user1Id, int user2Id)
            throws ResourceNotFoundException {
        throwIfUserIdsAreEqual(user1Id, user2Id);
        throwIfUserNotFound(user1Id);
        throwIfUserNotFound(user2Id);
        return userStorage.getCommonFriends(user1Id, user2Id);
    }

    private void changeBlankNameToLogin(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void throwIfUserNotFound(int userId) throws ResourceNotFoundException {
        if (userStorage.getById(userId) == null) {
            throw new ResourceNotFoundException("Not found user with id " + userId);
        }
    }

    private void throwIfUserIdsAreEqual(int user1Id, int user2Id) throws BadRequestException {
        if (user1Id == user2Id) {
            throw new BadRequestException("User ids are equal");
        }
    }
}
