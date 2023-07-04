package com.chiSoftware.testTask.serviceProviders;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.entities.contact.ContactRepository;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.security.JwtUtil;
import com.chiSoftware.testTask.services.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceProvider implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ContactRepository contactRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> authenticate(AuthRequest request){
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(request.getLogin());
        }
        catch (UsernameNotFoundException e){
            return new ResponseEntity<>("Username does not exist", HttpStatus.UNAUTHORIZED);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        Map<String, String> token = new HashMap<>();
        token.put("token", jwtUtil.generateToken(user));
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> createUser(User user) {
        if(user.getLogin()==null||user.getLogin().strip().equals("")){
            return new ResponseEntity<>("Login cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(user.getPassword()==null||user.getPassword().strip().equals("")){
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(userRepository.findUserByLogin(user.getLogin()).isPresent()){
            return new ResponseEntity<>("User with such login already exists", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("User was successfully created", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteUser(Principal principal) {
        Optional<User> optionalUser = userRepository.findUserByLogin(principal.getName());
        if(optionalUser.isEmpty()){
            return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
        }
        List<Contact> contacts = contactRepository.findContactsByOwner(principal.getName());
        contactRepository.deleteAll(contacts);
        userRepository.delete(optionalUser.get());
        return new ResponseEntity<>("User was successfully created", HttpStatus.NO_CONTENT);
    }
}
