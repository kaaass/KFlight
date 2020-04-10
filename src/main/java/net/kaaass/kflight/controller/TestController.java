package net.kaaass.kflight.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/")
    List<String> test() {
        return new ArrayList<>(){{
            add("Hello world");
            add("From kaaass~");
        }};
    }
}
