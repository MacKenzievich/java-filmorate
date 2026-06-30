package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    
    private final JdbcTemplate jdbcTemplate;
    private static final String CREATE_EVENT_SQL = """
            INSERT INTO events
            (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String GET_USER_FEED = """
            SELECT *
            FROM events
            WHERE user_id = ?
            ORDER BY event_id
            """;
    
    @Override
    public void createEvent(Event event){
        jdbcTemplate.update(
                CREATE_EVENT_SQL,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()
        );
    }
    
    @Override
    public List<Event> findByUserId(int user_id) {
        
        return jdbcTemplate.query(GET_USER_FEED, (rs, rowNum) -> {
           
            Event event = new Event();

            event.setEventId(rs.getLong("event_id"));
            event.setTimestamp(rs.getLong("timestamp"));
            event.setUserId(rs.getInt("user_id"));
            event.setEntityId(rs.getInt("entity_id"));

            event.setEventType(EventType.valueOf(
                    rs.getString("event_type")
            ));

            event.setOperation(Operation.valueOf(
                    rs.getString("operation")
            ));

            return event;

        }, user_id);
    }
}
