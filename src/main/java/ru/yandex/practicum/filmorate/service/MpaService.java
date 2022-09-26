package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public MpaService(MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<MpaRating> getAllMpa() {
        return mpaRatingStorage.getAll();
    }

    public MpaRating getMpaById(int mpaId) throws ResourceNotFoundException {
        MpaRating mpa = mpaRatingStorage.getById(mpaId);
        if (mpa == null) {
            throw new ResourceNotFoundException("Not found mpa rating with id " + mpaId);
        }

        return mpa;
    }
}
