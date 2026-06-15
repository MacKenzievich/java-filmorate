package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage storage;

    // Создаем тестовый фильм
    private Film createTestFilm() {
        return Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.G)
                .build();
    }

    @Test
    void createAndFindFilm_ShouldAddAndRetrieve() {
        // Создаем фильм
        Film film = createTestFilm();
        // Сохраняем его
        Film saved = storage.create(film);

        // Проверка, что у сохраненного фильма есть ID
        assertThat(saved.getId()).isNotNull();

        // Ищем по id
        Optional<Film> retrieved = storage.findFilmById(saved.getId());

        // Проверяем, что фильм найден и его свойства совпадают
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo(film.getName());
        assertThat(retrieved.get().getDescription()).isEqualTo(film.getDescription());
        // Можно добавить дополнительные проверки по другим полям
    }

    @Test
    void updateFilm_ShouldModifyExisting() {
        Film film = createTestFilm();
        Film saved = storage.create(film);

        saved.setName("Updated Name");
        storage.update(saved);

        Optional<Film> updated = storage.findFilmById(saved.getId());

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Name");
    }

    @Test
    void findAll_ShouldReturnList() {
        storage.create(createTestFilm());
        storage.create(createTestFilm());

        var films = storage.findAllFilms();
        assertThat(films).isNotEmpty();
        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findPopular_ShouldReturnList() {
        storage.create(createTestFilm());
        // Можно добавить еще фильмов с разным количеством лайков, если есть логика
        var popularFilms = storage.findPopular(10);
        assertThat(popularFilms).isNotNull();
        assertThat(popularFilms).isInstanceOf(List.class);
    }
}