package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {
    private final UserDbStorage userStorage;
    private static Random randomizer = new Random();

    @Test
    public void testGetUserById() {
        // Arrange
        User user = User.builder()
                .email("tester@mail.ru")
                .login("Tester")
                .name("IvanTester")
                .birthday(LocalDate.of(1992, 5, 13))
                .build();
        int userId = userStorage.add(user);
        user.setId(userId);

        // Act
        User userFromStorage = userStorage.getById(userId);

        // Assert
        assertNotNull(userFromStorage);
        assertEquals(user, userFromStorage);
    }

    @Test
    public void testUserNotFound() {
        User user = userStorage.getById(-1);

        assertNull(user);
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        User user = createTestUser();

        // Act
        user.setName("CoolTester");
        user.setLogin("CoolTester");
        userStorage.update(user);

        // Assert
        assertEquals(user, userStorage.getById(user.getId()));
    }

    @Test
    public void testAddToFriends() {
        // Arrange
        User user1 = createTestUser();
        User user2 = createTestUser();

        // Act
        userStorage.addToFriends(user1.getId(), user2.getId());

        // Assert
        List<User> friendsUser1 = userStorage.getFriends(user1.getId());
        assertEquals(1, friendsUser1.size());
        assertEquals(user2, friendsUser1.get(0));
    }

    @Test
    public void testRemoveFromFriends() {
        // Arrange
        User user1 = createTestUser();
        User user2 = createTestUser();

        // Act
        userStorage.addToFriends(user1.getId(), user2.getId());
        userStorage.removeFromFriends(user1.getId(), user2.getId());

        // Assert
        List<User> friendsUser1 = userStorage.getFriends(user1.getId());
        assertEquals(0, friendsUser1.size());
    }

    @Test
    public void testGetCommonFriends() {
        // Arrange
        User user1 = createTestUser();
        User user2 = createTestUser();
        User user3 = createTestUser();

        // Act
        userStorage.addToFriends(user1.getId(), user3.getId());
        userStorage.addToFriends(user2.getId(), user3.getId());
        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());

        // Assert
        assertEquals(1, commonFriends.size());
        assertEquals(user3, commonFriends.get(0));
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        int usersBefore = userStorage.getAll().size();
        User user1 = createTestUser();
        User user2 = createTestUser();

        // Act
        List<User> users = userStorage.getAll();

        // Assert
        assertEquals(usersBefore + 2, users.size());
        assertEquals(user1, users.get(usersBefore));
        assertEquals(user2, users.get(usersBefore + 1));
    }

    private User createTestUser() {
        int randomNumber = randomizer.nextInt(1000);
        User user = User.builder()
                .email("tester" + randomNumber + "@mail.ru")
                .login("Tester" + randomNumber)
                .name("Tester" + randomNumber)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        int userId = userStorage.add(user);
        user.setId(userId);
        return user;
    }

}
