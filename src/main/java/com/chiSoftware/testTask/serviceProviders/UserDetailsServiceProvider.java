package com.chiSoftware.testTask.serviceProviders;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserDetails;
import com.chiSoftware.testTask.entities.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceProvider implements org.springframework.security.core.userdetails.UserDetailsService{
    @Autowired
    private final UserRepository userRepository;
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByLogin(username);

        if(user.isEmpty())
            throw new UsernameNotFoundException("User Not Found");

        return new UserDetails(user.get());
    }
}