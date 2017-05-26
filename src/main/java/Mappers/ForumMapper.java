package Mappers;

import Entities.ForumEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<ForumEntity> {

    @Override
    public ForumEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        final ForumEntity forumEntity = new ForumEntity();

        forumEntity.setTitle(resultSet.getString("title"));
        forumEntity.setUser(resultSet.getString("user"));
        forumEntity.setSlug(resultSet.getString("slug"));
        forumEntity.setPosts(resultSet.getInt("posts"));
        forumEntity.setThreads(resultSet.getInt("threads"));

        return forumEntity;
    }
}
