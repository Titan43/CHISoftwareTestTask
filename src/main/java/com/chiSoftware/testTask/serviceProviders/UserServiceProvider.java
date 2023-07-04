package com.chiSoftware.testTask.serviceProviders;

import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceProvider implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> authenticate(AuthRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<String> createUser(User user) {
        if(user.getUsername()==null||user.getUsername().strip().equals("")){
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(user.getPassword()==null||user.getPassword().strip().equals("")){
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("User was successfully created", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteUser(Principal principal) {
        Optional<User> optionalUser = userRepository.findUserByUsername(principal.getName());
        if(optionalUser.isEmpty()){
            return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(optionalUser.get());
        return new ResponseEntity<>("User was successfully created", HttpStatus.NO_CONTENT);
    }

}
