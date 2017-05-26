package Controllers;

import DAOs.ServiceDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/service/")
public class ServiceController {
    private final ServiceDAO dbService;

    public ServiceController(JdbcTemplate jdbcTemplate) {
        this.dbService = new ServiceDAO(jdbcTemplate);
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity<String> clear() {
        return (dbService.clear());
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public ResponseEntity<String> getStatus() {
        return (dbService.getInfo());
    }
}
