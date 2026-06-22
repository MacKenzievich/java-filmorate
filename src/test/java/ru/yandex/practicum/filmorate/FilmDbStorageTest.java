package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.time.LocalDate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(FilmDbStorage.class)
class FilmDbStorageTest {

    private final FilmDbStorage storage;
    private int insertedFilmId;

    @BeforeEach
    public void setUp() {
        Film film = createTestFilm();
        Film savedFilm = storage.create(film);
        insertedFilmId = savedFilm.getId();
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
        Film film = createTestFilm();

        Film savedFilm = storage.create(film);

        assertThat(savedFilm.getId()).isNotNull();

        assertThat(savedFilm)
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Test Description")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1))
                .hasFieldOrPropertyWithValue("duration", 120);
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

    @Test
    public void testUpdateFilm() {
        Film updatedFilm = createTestFilm();
        updatedFilm.setId(insertedFilmId);
        updatedFilm.setName("Updated Name");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setDuration(150);
        storage.update(updatedFilm);
        Optional<Film> filmFromDb = storage.findFilmById(insertedFilmId);
        assertThat(filmFromDb).isPresent();
        assertThat(filmFromDb.get())
                .hasFieldOrPropertyWithValue("name", "Updated Name")
                .hasFieldOrPropertyWithValue("description", "Updated Description")
                .hasFieldOrPropertyWithValue("duration", 150);
    }

    @Test
    public void testFindAllFilms() {
        var films = storage.findAllFilms();
        assertThat(films).isNotEmpty();
        assertThat(films).anySatisfy(film -> assertThat(film).hasFieldOrPropertyWithValue("id", insertedFilmId));
    }

    @Test
    public void testFindPopular() {
        int count = 3;
        var popularFilms = storage.findPopular(count);
        assertThat(popularFilms).hasSizeLessThanOrEqualTo(count);
    }

}