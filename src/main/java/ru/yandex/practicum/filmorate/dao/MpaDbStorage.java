package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlFileReader fileReader;

    private String findAllMpaSql;
    private String findMpaByIdSql;

    @PostConstruct
    public void init() {
        findAllMpaSql = fileReader.readSqlFile("/mpa/findAllMpa.sql");
        findMpaByIdSql = fileReader.readSqlFile("/mpa/findMpaById.sql");
    }

    @Override
    public List<Mpa> findAllMpa() {
        String sql = findAllMpaSql;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> findMpaById(int id) {
        String sql = findMpaByIdSql;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id).stream().findFirst();
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}