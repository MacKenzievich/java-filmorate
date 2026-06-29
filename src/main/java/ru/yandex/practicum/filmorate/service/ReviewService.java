package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review create(Review review) {
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());
        review.setReviewId(null);
        review.setUseful(0);
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("Не указан идентификатор отзыва.");
        }
        Review savedReview = findById(review.getReviewId());
        savedReview.setContent(review.getContent());
        savedReview.setIsPositive(review.getIsPositive());
        return reviewStorage.update(savedReview);
    }

    public void delete(int reviewId) {
        findById(reviewId);
        reviewStorage.delete(reviewId);
    }

    public Review findById(int reviewId) {
        return reviewStorage.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден."));
    }

    public List<Review> findAll(Integer filmId, int count) {
        if (count <= 0) {
            throw new ValidationException("Количество отзывов должно быть больше нуля.");
        }
        if (filmId != null) {
            validateFilm(filmId);
        }
        return reviewStorage.findAll(filmId, count);
    }

    public void addLike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteVote(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.deleteVote(reviewId, userId);
    }

    private void validateReviewAndUser(int reviewId, int userId) {
        findById(reviewId);
        validateUser(userId);
    }

    private void validateUser(Integer userId) {
        if (userId == null || userStorage.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }

    private void validateFilm(Integer filmId) {
        if (filmId == null || filmStorage.findFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException("Фильм не найден.");
        }
    }
}
