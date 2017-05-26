package Mappers;

import Entities.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<UserEntity> {

    @Override
    public UserEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setNickname(resultSet.getString("nickname"));
        userEntity.setFullname(resultSet.getString("fullname"));
        userEntity.setAbout(resultSet.getString("about"));
        userEntity.setEmail(resultSet.getString("email"));

        return userEntity;
    }
}
