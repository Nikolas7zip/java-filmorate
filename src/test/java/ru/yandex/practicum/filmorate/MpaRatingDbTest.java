package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/data.sql")
public class MpaRatingDbTest {
    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Test
    public void testGetMpaById() {
        Optional<MpaRating> mpaRatingOptional = mpaRatingDbStorage.getById(1);

        assertTrue(mpaRatingOptional.isPresent());
        MpaRating mpa = mpaRatingOptional.get();
        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    public void testGetAllMpa() {
        List<MpaRating> mpaRatings = mpaRatingDbStorage.getAll();

        assertEquals(5, mpaRatings.size());
    }
}
