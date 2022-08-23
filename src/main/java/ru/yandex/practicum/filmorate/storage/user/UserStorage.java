package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> getAll();
    User getById(Integer id);
    User add(User user);
    User update(User user);
    Set<Integer> getFriendsId(Integer id);
    void addToFriends(Integer targetId, Integer friendId);
    boolean removeFromFriends(Integer targetId, Integer friendId);
    void throwIfNotFound(Integer id) throws ResourceNotFoundException;
}
