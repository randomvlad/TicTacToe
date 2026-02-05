package tictactoe.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppUserLoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
