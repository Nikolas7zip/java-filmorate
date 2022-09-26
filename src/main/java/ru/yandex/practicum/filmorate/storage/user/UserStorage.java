package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();
    User getById(int id);
    int add(User user);
    void update(User user);
    List<User> getFriends(int userId);
    boolean addToFriends(int userId, int friendId);
    boolean removeFromFriends(int userId, int friendId);
    List<User> getCommonFriends(int user1Id, int user2Id);
}
