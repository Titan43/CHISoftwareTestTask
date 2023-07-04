package com.chiSoftware.testTask.controllers;

import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @PostMapping(path = "/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request){
        return userService.authenticate(request);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteUser(Principal principal) {
        return userService.deleteUser(principal);
    }
}
