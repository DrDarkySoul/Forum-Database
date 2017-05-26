package DAOs;

import Entities.ForumEntity;
import Entities.ThreadEntity;
import Entities.UserEntity;
import Helpers.DateFix;
import Mappers.ForumMapper;
import Mappers.ThreadMapper;
import Mappers.UserMapper;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;

public class ForumDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<String> createForum(ForumEntity forumEntity) {
        try {
            final UserEntity userEntity = new UserDAO(jdbcTemplate).getUserEntity(forumEntity.getUser());
            forumEntity.setUser(userEntity.getNickname());
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        final ForumEntity forumEntityNew = new ForumDAO(jdbcTemplate).getForumEntity(forumEntity.getSlug());
        if (forumEntityNew != null)
            return new ResponseEntity<>(forumEntityNew.getJSON().toString(), HttpStatus.CONFLICT);
        jdbcTemplate.update("INSERT INTO forum (title,\"user\",slug,posts,threads) VALUES(?,?,?,?,?)",
                forumEntity.getTitle(), forumEntity.getUser(), forumEntity.getSlug(), forumEntity.getPosts(), forumEntity.getThreads());
        return new ResponseEntity<>(forumEntity.getJSON().toString(), HttpStatus.CREATED);
    }

    ForumEntity getForumEntity(String slug) {
        try {
            final ForumEntity forum = jdbcTemplate.queryForObject("SELECT * FROM forum WHERE LOWER(slug) = LOWER(?)",
                    new Object[]{slug}, new ForumMapper());
            if(forum.getPosts() == 0)
                forum.setPosts(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE LOWER(forum) = LOWER(?)",
                        new Object[]{slug}, Integer.class));
            if(forum.getThreads() == 0)
                forum.setThreads(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM thread WHERE LOWER(forum) = LOWER(?)",
                        new Object[]{slug}, Integer.class));
            return forum;
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseEntity<String> getForumDetails(String slug) {
        try {
            final ForumEntity forum = jdbcTemplate.queryForObject("SELECT * FROM forum WHERE LOWER(slug) = LOWER(?)",
                    new Object[]{slug}, new ForumMapper());
            if(forum.getPosts() == 0)
                forum.setPosts(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE LOWER(forum) = LOWER(?)",
                        new Object[]{slug}, Integer.class));
            if(forum.getThreads() == 0)
                forum.setThreads(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM thread WHERE LOWER(forum) = LOWER(?)",
                        new Object[]{slug}, Integer.class));
            return new ResponseEntity<>(forum.getJSON().toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> createThread(ThreadEntity threadEntity, String slug) {
        try {
            final ThreadEntity threadEntityNew = new ThreadDAO(jdbcTemplate).getThreadEntityBySlug(threadEntity.getSlug());
            if (threadEntityNew != null) {
                threadEntityNew.setCreated(DateFix.transformWithAppend00(threadEntityNew.getCreated()));
                return new ResponseEntity<>(threadEntityNew.getJSON().toString(), HttpStatus.CONFLICT);
            }
        } catch (Exception ignored) {}
        final ForumEntity forumEntity;
        try {
            final UserEntity userEntity = new UserDAO(jdbcTemplate).getUserEntity(threadEntity.getAuthor());
            forumEntity = this.getForumEntity(slug);
            if(userEntity == null ||
                    forumEntity == null)
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            threadEntity.setForum(forumEntity.getSlug());
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO thread (title,author,forum,message,slug," +
                            "votes,created) VALUES (?,?,?,?,?,?,?::timestamptz)", new String[]{"id"});
            preparedStatement.setString(1, threadEntity.getTitle());
            preparedStatement.setString(2, threadEntity.getAuthor());
            preparedStatement.setString(3, threadEntity.getForum());
            preparedStatement.setString(4, threadEntity.getMessage());
            preparedStatement.setString(5, threadEntity.getSlug());
            preparedStatement.setInt(6, threadEntity.getVotes());
            preparedStatement.setString(7, threadEntity.getCreated());
            return preparedStatement;
        }, keyHolder);
        threadEntity.setId((int) keyHolder.getKey());
        jdbcTemplate.update("UPDATE forum SET threads = threads + 1 WHERE LOWER(slug) = LOWER(?)", slug);
        return new ResponseEntity<>(threadEntity.getJSON().toString(), HttpStatus.CREATED);
    }


    public ResponseEntity<String> getThreads(String slug, Integer limit, String since, Boolean desc) {
        final ResponseEntity<String> forum = getForumDetails(slug);
        if (forum.getStatusCode() == HttpStatus.NOT_FOUND) return forum;
        final StringBuilder query = new StringBuilder("SELECT * FROM thread WHERE forum=?");
        if (since != null) {
            since = DateFix.replaceOnSpace(since);
            if (desc != null && desc) query.append(" AND created <=?::timestamptz ");
            else query.append(" AND created >=?::timestamptz ");
        }
        query.append(" ORDER BY created ");
        if (desc != null && desc) query.append(" DESC ");
        final List<ThreadEntity> threadEntityList;
        if (limit != null) {
            query.append(" LIMIT ?");
            if (since != null)
                threadEntityList = jdbcTemplate.query(query.toString(), new Object[]{slug, since, limit}, new ThreadMapper());
            else
                threadEntityList = jdbcTemplate.query(query.toString(), new Object[]{slug, limit}, new ThreadMapper());
        } else
            if (since != null)
                threadEntityList = jdbcTemplate.query(query.toString(), new Object[]{slug, since}, new ThreadMapper());
            else
                threadEntityList = jdbcTemplate.query(query.toString(), new Object[]{slug}, new ThreadMapper());
        final JSONArray result = new JSONArray();
        threadEntityList.forEach(threadEntity -> {
            threadEntity.setCreated(DateFix.transformWithAppend00(threadEntity.getCreated()));
            result.put(threadEntity.getJSON());});
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getForumUsers(String slug, Integer limit, String since, Boolean desc) {
        final ResponseEntity<String> forum = getForumDetails(slug);
        if (forum.getStatusCode() == HttpStatus.NOT_FOUND) return forum;
        final StringBuilder query = new StringBuilder(
                "SELECT *, OCTET_LENGTH(LOWER(nickname)) FROM users WHERE nickname IN")
                .append("(SELECT u.nickname FROM users as u FULL OUTER JOIN post as p ")
                .append("ON LOWER(u.nickname) = LOWER(p.author) FULL OUTER JOIN thread as t ")
                .append("ON LOWER(u.nickname) = LOWER(t.author) WHERE LOWER(p.forum) = LOWER(?) ")
                .append("OR LOWER(t.forum) = LOWER(?) GROUP BY u.nickname)");
        if (since != null)
            if (desc != null && desc) query.append(" AND nickname<'").append(since).append("'");
            else query.append(" AND nickname>'").append(since).append("'");
        query.append("  ORDER BY nickname");
        if (desc != null && desc) query.append(" DESC");
        if (limit != null) query.append(" LIMIT ").append(limit);
        final List<UserEntity> userEntityList = jdbcTemplate.query(query.toString(), new Object[]{slug, slug}, new UserMapper());
        final JSONArray result = new JSONArray();
        userEntityList.forEach(userEntity -> result.put(userEntity.getJSON()));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }
}
