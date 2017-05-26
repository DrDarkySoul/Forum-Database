package Controllers;

import DAOs.ForumDAO;
import Entities.ForumEntity;
import Entities.ThreadEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum/")
public class ForumController {
    private final ForumDAO forumDAO;

    public ForumController(JdbcTemplate jdbcTemplate) {
        this.forumDAO = new ForumDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<String> createForum(@RequestBody ForumEntity body) {
        return (forumDAO.createForum(body));
    }

    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<String> createThread(@RequestBody ThreadEntity body,
                                               @PathVariable(name = "slug") String slug) {
        return (forumDAO.createThread(body, slug));
    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<String> getForumDetails(@PathVariable(name = "slug") String slug) {
        return (forumDAO.getForumDetails(slug));
    }

    @RequestMapping(path = "/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity<String> getForumThreads(@PathVariable String slug,
                                                  @RequestParam(value = "limit", required = false) Integer limit,
                                                  @RequestParam(value = "since", required = false) String since,
                                                  @RequestParam(value = "desc", required = false) Boolean desc) {
        return (forumDAO.getThreads(slug, limit, since, desc));
    }

    @RequestMapping(path = "/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity<String> getForumUsers(@PathVariable(name = "slug") String slug,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "since", required = false) String since,
                                                @RequestParam(value = "desc", required = false) Boolean desc){
        return (forumDAO.getForumUsers(slug, limit, since, desc));
    }
}
