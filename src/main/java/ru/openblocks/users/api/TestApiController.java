package ru.openblocks.users.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

    @GetMapping("/api/v1/hello")
    public String hello(@AuthenticationPrincipal Jwt principal) {
        return "Hello world!";
    }
}
