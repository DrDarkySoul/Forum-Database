package Mappers;

import Entities.ThreadEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadMapper implements RowMapper<ThreadEntity> {

    @Override
    public ThreadEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        final ThreadEntity threadEntity = new ThreadEntity();

        threadEntity.setId(resultSet.getInt("id"));
        threadEntity.setTitle(resultSet.getString("title"));
        threadEntity.setAuthor(resultSet.getString("author"));
        threadEntity.setForum(resultSet.getString("forum"));
        threadEntity.setMessage(resultSet.getString("message"));
        threadEntity.setSlug(resultSet.getString("slug"));
        threadEntity.setCreated(resultSet.getString("created"));

        if (resultSet.getInt("votes") != 0)
            threadEntity.setVotes(resultSet.getInt("votes"));

        return threadEntity;
    }
}
