package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.time.LocalDate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(FilmDbStorage.class)
class FilmDbStorageTest {

    private final FilmDbStorage storage;
    private int insertedFilmId;

    @BeforeEach
    public void setUp() {
        // создаем и вставляем фильм
        Film film = createTestFilm();
        Film savedFilm = storage.create(film);
        insertedFilmId = savedFilm.getId(); // запоминаем ID для поиска
    }


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
    public void testCreateFilm() {
        // Создаем тестовый фильм
        Film film = createTestFilm(); // Название: "Test Film", описание: "Test Description" и т.д.

        // Вставляем в базу
        Film savedFilm = storage.create(film);

        // Проверяем, что ID присвоен (база вернула айди после вставки)
        assertThat(savedFilm.getId()).isNotNull();

        // Проверяем, что все поля верно сохранены
        assertThat(savedFilm)
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Test Description")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1))
                .hasFieldOrPropertyWithValue("duration", 120);
        // Проверяем, что MPA тоже правильное
        assertThat(savedFilm.getMpa()).isEqualTo(Mpa.G);
    }

    @Test
    public void testFindUserById() {
        Optional<Film> userOptional = storage.findFilmById(insertedFilmId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", insertedFilmId)
                );
    }

}