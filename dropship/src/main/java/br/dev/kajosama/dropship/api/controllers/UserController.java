package br.dev.kajosama.dropship.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.responses.SimpleUserResponse;
import br.dev.kajosama.dropship.api.services.UserService;
import br.dev.kajosama.dropship.domain.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SimpleUserResponse registerAccount(@Valid @RequestBody User user) {

        String rawPassword = user.getPassword();

        userService.registerAccount(user, rawPassword);
        return SimpleUserResponse.fromEntity(user);
    }

}
