package com.chiSoftware.testTask.serviceTests;

import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class DetailsServiceTests {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void testLoadUserByUsername(){
        UserDetails expected =
                new com.chiSoftware.testTask.entities.user.UserDetails(new User());
        when(userRepository.findUserByLogin(any(String.class)))
                .thenReturn(Optional.of(new User()));
        UserDetails actual = userDetailsService.loadUserByUsername("test");

        assertEquals(expected.getUsername(), actual.getUsername());
    }
}
