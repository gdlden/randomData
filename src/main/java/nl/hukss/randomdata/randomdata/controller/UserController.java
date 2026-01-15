package nl.hukss.randomdata.randomdata.controller;

import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.User;
import nl.hukss.randomdata.randomdata.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user);
    }
}
