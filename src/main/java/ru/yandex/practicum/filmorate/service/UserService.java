package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Integer userId) throws ResourceNotFoundException {
        userStorage.throwIfNotFound(userId);

        return userStorage.getById(userId);
    }

    public User createUser(User user) {
        changeBlankNameToLogin(user);
        User userFromStorage = userStorage.add(user);
        log.info("Success create {}", userFromStorage);

        return userFromStorage;
    }

    public User updateUser(User user) throws ResourceNotFoundException {
        userStorage.throwIfNotFound(user.getId());
        changeBlankNameToLogin(user);
        User userFromStorage = userStorage.update(user);
        log.info("Success update {}", userFromStorage);

        return userFromStorage;
    }

    public List<User> getUserFriends(Integer userId) throws ResourceNotFoundException {
        userStorage.throwIfNotFound(userId);

        return collectFriendsToList(userStorage.getFriendsId(userId));
    }

    public void addUsersToFriends(Integer user1Id, Integer user2Id) throws ResourceNotFoundException {
        userStorage.throwIfNotFound(user1Id);
        userStorage.throwIfNotFound(user2Id);
        userStorage.addToFriends(user1Id, user2Id);
        userStorage.addToFriends(user2Id, user1Id);
        log.info("Add users {} and {} to friends mutually", user1Id, user2Id);
    }

    public void removeUsersFromFriends(Integer user1Id, Integer user2Id) throws ResourceNotFoundException {
        userStorage.throwIfNotFound(user1Id);
        userStorage.throwIfNotFound(user2Id);
        boolean isRemoveFriendForUser1 = userStorage.removeFromFriends(user1Id, user2Id);
        boolean isRemoveFriendForUser2 = userStorage.removeFromFriends(user2Id, user1Id);
        if (isRemoveFriendForUser1 && isRemoveFriendForUser2) {
            log.info("Remove users {} and {} from friends mutually", user1Id, user2Id);
        } else {
            throw new ResourceNotFoundException("Could not find friendships between users " + user1Id +
                    " and " + user2Id);
        }
    }

    public List<User> findCommonFriendsBetweenTwoUsers(Integer user1Id, Integer user2Id)
            throws ResourceNotFoundException {
        userStorage.throwIfNotFound(user1Id);
        userStorage.throwIfNotFound(user2Id);
        Set<Integer> friendsIdUser1 = userStorage.getFriendsId(user1Id);
        Set<Integer> friendsIdUser2 = userStorage.getFriendsId(user2Id);
        if (friendsIdUser1 != null && friendsIdUser2 != null) {
            friendsIdUser1.retainAll(friendsIdUser2);
            return collectFriendsToList(friendsIdUser1);
        }

        return Collections.emptyList();
    }

    private void changeBlankNameToLogin(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private List<User> collectFriendsToList(Set<Integer> friendsId) throws ResourceNotFoundException {
        if (friendsId == null) {
            throw new ResourceNotFoundException("Not found friends");
        }

        List<User> userFriends = new ArrayList<>();
        for (Integer id : friendsId) {
            userFriends.add(userStorage.getById(id));
        }

        return userFriends;
    }
}
