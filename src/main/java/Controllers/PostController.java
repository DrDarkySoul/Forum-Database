package Controllers;

import DAOs.PostDAO;
import Entities.PostEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/post/")
public class PostController {

    private final PostDAO postDAO;

    public PostController(JdbcTemplate jdbcTemplate) {
        this.postDAO = new PostDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity<String> getPostDetail(@PathVariable(name = "id") Integer id,
                                                @RequestParam(value = "related",
                                                                defaultValue = "") String related) {
        return (postDAO.get(id, related));
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.POST)
    public ResponseEntity<String> changePostDetail(@PathVariable(name = "id") Integer id,
                                                   @RequestBody PostEntity body) {
        return (postDAO.update(id, body));
    }
}
