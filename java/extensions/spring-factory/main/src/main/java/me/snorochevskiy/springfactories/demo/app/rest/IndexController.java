package me.snorochevskiy.springfactories.demo.app.rest;

import me.snorochevskiy.springfactories.demo.daytime.DaytimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private DaytimeService daytimeService;

    @GetMapping("/")
    public String index() {
        return daytimeService.now();
    }
}
