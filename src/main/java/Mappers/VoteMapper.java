package Mappers;

import Entities.VoteEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteMapper implements RowMapper<VoteEntity> {

    @Override
    public VoteEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        final VoteEntity voteEntity = new VoteEntity();

        voteEntity.setThreadId(resultSet.getInt("id"));
        voteEntity.setNickname(resultSet.getString("nickname"));
        voteEntity.setSlug(resultSet.getString("slug"));
        voteEntity.setVoice(resultSet.getInt("voice"));

        return voteEntity;
    }
}
