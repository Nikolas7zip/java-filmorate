package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();
    private int id = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(id, user);

        return user;
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            return user;
        }

        return null;
    }

    @Override
    public Set<Integer> getFriendsId(Integer id) {
        if (friends.containsKey(id)) {
            return new HashSet<>(friends.get(id));
        }

        return null;
    }

    @Override
    public void addToFriends(Integer targetId, Integer friendId) {
        if (friends.containsKey(targetId)) {
            friends.get(targetId).add(friendId);
        } else {
            Set<Integer> setFriends = new HashSet<>();
            setFriends.add(friendId);
            friends.put(targetId, setFriends);
        }
    }

    @Override
    public boolean removeFromFriends(Integer targetId, Integer friendId) {
        if (friends.containsKey(targetId)) {
            return friends.get(targetId).remove(friendId);
        }

        return false;
    }

    @Override
    public void throwIfNotFound(Integer id) throws ResourceNotFoundException {
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("Not found user with id " + id);
        }
    }
}
