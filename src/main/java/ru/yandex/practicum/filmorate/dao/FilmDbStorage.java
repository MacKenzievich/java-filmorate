package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlFileReader fileReader;

    private String selectFilmsSql;
    private String insertFilmSql;
    private String insertFilmGenreSql;
    private String selectFilmByIdSql;
    private String deleteFilmGenresSql;
    private String selectPopularFilmsSql;
    private String updateFilmSql;
    private String selectAllFilmsSql;

    @PostConstruct
    public void init() {
        selectFilmsSql = fileReader.readSqlFile("/film/selectFilm.sql");
        insertFilmSql = fileReader.readSqlFile("/film/insertFilm.sql");
        insertFilmGenreSql = fileReader.readSqlFile("/film/insertFilmGenre.sql");
        selectFilmByIdSql = fileReader.readSqlFile("/film/selectFilmById.sql");
        deleteFilmGenresSql = fileReader.readSqlFile("/film/deleteFilmGenres.sql");
        selectPopularFilmsSql = fileReader.readSqlFile("/film/selectPopularFilms.sql");
        updateFilmSql = fileReader.readSqlFile("/film/updateFilm.sql");
        selectAllFilmsSql = fileReader.readSqlFile("/film/selectAllFilms.sql");
    }


    @Override
    public Film create(Film film) {
        String sql = insertFilmSql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateGenres(film.getGenres(), film.getId());
        return film;
    }


    @Override
    public Film update(Film film) {
        int id = film.getId();
        String sql = updateFilmSql;
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), id);
        updateGenres(film.getGenres(), id);
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = selectAllFilmsSql;
        return jdbcTemplate.query(selectFilmsSql + " " + sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = selectFilmByIdSql;
        return jdbcTemplate.query(selectFilmsSql + " " + sql, (rs, rowNum) -> makeFilm(rs), id).stream().findFirst();
    }

    @Override
    public List<Film> findPopular(int count) {
        String sql = selectPopularFilmsSql;
        return jdbcTemplate.query(selectFilmsSql + " " + sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("film_id");
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name")))
                .build();
        return film;
    }

    private void updateGenres(Set<Genre> genres, int id) {
        jdbcTemplate.update(deleteFilmGenresSql, id);
        if (genres.size() > 0) {
            String sql = insertFilmGenreSql;
            Genre[] g = genres.toArray(new Genre[genres.size()]);
            jdbcTemplate.batchUpdate(
                    sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, id);
                            ps.setInt(2, g[i].getId());
                        }

                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }


}