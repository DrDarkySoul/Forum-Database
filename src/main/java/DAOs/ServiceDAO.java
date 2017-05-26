package DAOs;

import Entities.ServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public class ServiceDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<String> getInfo() {
        final Integer thread_like_post = jdbcTemplate.queryForObject("SELECT count(*) FROM thread WHERE id NOT IN (SELECT DISTINCT thread FROM post)", new Object[]{}, Integer.class);
        final Integer forum = jdbcTemplate.queryForObject ("SELECT COUNT(*) FROM forum", new Object[]{}, Integer.class);
        final Integer post = jdbcTemplate.queryForObject  ("SELECT COUNT(*) FROM post", new Object[]{}, Integer.class) + thread_like_post;
        final Integer thread = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM thread", new Object[]{}, Integer.class) - thread_like_post;
        final Integer user = jdbcTemplate.queryForObject  ("SELECT COUNT(*) FROM users", new Object[]{}, Integer.class);
        return new ResponseEntity<>(new ServiceEntity(forum, post, thread, user).getJSONString(), HttpStatus.OK);
    }

    public ResponseEntity<String> clear() {
        jdbcTemplate.update("DELETE FROM users; " + "DELETE FROM post; " + "DELETE FROM thread; " +
                        "DELETE FROM forum; " + "DELETE FROM vote;");
        return new ResponseEntity<>(new ServiceEntity().getJSONString(), HttpStatus.OK);
    }
}
