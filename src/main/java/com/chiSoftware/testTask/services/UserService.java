package com.chiSoftware.testTask.services;

import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.AuthRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface UserService {
    ResponseEntity<?> authenticate(AuthRequest request);
    ResponseEntity<String> createUser(User user);
    ResponseEntity<String> deleteUser(Principal principal);
}
