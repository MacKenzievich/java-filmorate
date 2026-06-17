package ru.yandex.practicum.filmorate.dao;


import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SqlFileReader {
    public String readSqlFile(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("sql/" + filename);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении SQL файла: " + filename, e);
        }
    }
}

