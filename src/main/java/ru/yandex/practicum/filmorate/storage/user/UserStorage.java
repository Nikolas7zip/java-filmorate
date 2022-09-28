package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {
    List<User> getFriends(long userId);
    boolean addToFriends(long userId, long friendId);
    boolean removeFromFriends(long userId, long friendId);
    List<User> getCommonFriends(long user1Id, long user2Id);
}
