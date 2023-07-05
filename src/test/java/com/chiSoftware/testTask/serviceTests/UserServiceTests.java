package com.chiSoftware.testTask.serviceTests;

import com.chiSoftware.testTask.entities.contact.ContactRepository;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserServiceTests {

	@Test
	void contextLoads() {
	}

}
