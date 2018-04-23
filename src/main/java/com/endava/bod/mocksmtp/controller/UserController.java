package com.endava.bod.mocksmtp.controller;

import com.endava.bod.mocksmtp.domain.Activation;
import com.endava.bod.mocksmtp.domain.User;
import com.endava.bod.mocksmtp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController("/api/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public String createUser(@RequestBody User user) {
        return userService.create(user)
                .map(ok -> "User created successfully")
                .getOrElseThrow(error -> new RuntimeException("Failed: User couldn't be created "+error.getMessage(), error));
    }

    @GetMapping("/api/users/{user}/activate/{activationCode}")
    public String activateUser(@PathVariable String user, @PathVariable String activationCode){
        return userService.activate(new Activation(activationCode,user))
                .map(ok -> String.format("User %s activated successfully.",user))
                .getOrElseThrow(error -> new RuntimeException("Failed: User couldn't be activated "+error.getMessage(), error));
    }
}
