package Mappers;

import Entities.PostEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<PostEntity> {

    @Override
    public PostEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        final PostEntity postEntity = new PostEntity();

        postEntity.setId(resultSet.getInt("id"));
        postEntity.setAuthor(resultSet.getString("author"));
        postEntity.setCreated(resultSet.getString("created"));
        postEntity.setForum(resultSet.getString("forum"));
        postEntity.setEdited(resultSet.getBoolean("isEdited"));
        postEntity.setMessage(resultSet.getString("message"));
        postEntity.setParent(resultSet.getInt("parent"));
        postEntity.setThread(resultSet.getInt("thread"));
        postEntity.setPath(resultSet.getString("path"));

        return postEntity;
    }
}
