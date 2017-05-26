package Controllers;

import DAOs.UserDAO;
import Entities.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
public class UserController {

    private final UserDAO userService;

    UserController(JdbcTemplate jdbcTemplate) {
        this.userService = new UserDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/{nickname}/create",
            method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserEntity body,
                                             @PathVariable(name = "nickname") String nickname) {
        return (userService.create(body, nickname));
    }

    @RequestMapping(path = "/{nickname}/profile",
            method = RequestMethod.GET)
    public ResponseEntity<String> getUser(@PathVariable(name = "nickname") String nickname) {
        return (userService.get(nickname));
    }

    @RequestMapping(path = "/{nickname}/profile",
            method = RequestMethod.POST)
    public ResponseEntity<String> updateUser(@RequestBody UserEntity body,
                                             @PathVariable(name = "nickname") String nickname) {
        return (userService.update(body, nickname));
    }
}
