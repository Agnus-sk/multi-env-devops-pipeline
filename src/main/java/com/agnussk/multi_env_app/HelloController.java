package com.agnussk.multi_env_app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${app.env:DEFAULT}")
    private String environment;

    @GetMapping("/")
    public String hello() {
        return "Hello from " + environment + " Environment!";
    }
}
