package nl.hukss.randomdata.randomdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/login")
    public String login() {
        return "forward:/index.html";
    }

    @GetMapping("/app")
    public String app() {
        return "forward:/index.html";
    }
}
