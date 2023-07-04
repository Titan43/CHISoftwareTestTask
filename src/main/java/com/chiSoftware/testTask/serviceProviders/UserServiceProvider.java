package com.chiSoftware.testTask.serviceProviders;

import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserServiceProvider implements UserService {

    @Override
    public ResponseEntity<?> authenticate(AuthRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<String> createUser(User user) {

        return null;
    }

    @Override
    public ResponseEntity<String> deleteUser(Principal principal) {
        return null;
    }

}
