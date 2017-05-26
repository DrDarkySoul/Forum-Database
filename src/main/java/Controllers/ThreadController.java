package Controllers;

import DAOs.ThreadDAO;
import Entities.PostEntity;
import Entities.ThreadEntity;
import Entities.VoteEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/thread/")
public class ThreadController {

    private final ThreadDAO threadDAO;

    public ThreadController(JdbcTemplate jdbcTemplate) {
        this.threadDAO = new ThreadDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST)
    public ResponseEntity<String> createPost(@PathVariable(name = "slug_or_id") String slug_or_id,
                                             @RequestBody ArrayList<PostEntity> body) {
        return (threadDAO.createPosts(body, slug_or_id));
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity<String> getThreadDetails(@PathVariable(name = "slug_or_id") String slug_or_id) {
        return (threadDAO.getThreadDetails(slug_or_id));
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity<String> updateThread(@PathVariable(name = "slug_or_id") String slug_or_id,
                                               @RequestBody ThreadEntity body) {
        return (threadDAO.updateThread(body, slug_or_id));
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity<String> getThreadPosts(@PathVariable(name = "slug_or_id") String slug_or_id,
                                                 @RequestParam(value = "limit", required = false) Integer limit,
                                                 @RequestParam(value = "sort", required = false) String sort,
                                                 @RequestParam(value = "desc", required = false) boolean desc,
                                                 @RequestParam(value = "marker", required = false, defaultValue = "0") Integer marker) {
        return (threadDAO.getThreadPosts(slug_or_id, limit, sort, desc, marker));
    }

    @RequestMapping(path = "/{slug_or_id}/vote", method = RequestMethod.POST)
    public ResponseEntity<String> voteThread(@PathVariable(name = "slug_or_id") String slug_or_id,
                                             @RequestBody VoteEntity body) {
        return (threadDAO.vote(body, slug_or_id));
    }
}
