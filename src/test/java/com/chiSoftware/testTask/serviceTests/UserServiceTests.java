package com.chiSoftware.testTask.serviceTests;

import com.chiSoftware.testTask.entities.contact.ContactRepository;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.security.JwtUtil;
import com.chiSoftware.testTask.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private ContactRepository contactRepository;

	@MockBean
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testAuthenticateFail() {
		AuthRequest authRequest = new AuthRequest("test", "test");
		ResponseEntity<?> expectedResponse = new ResponseEntity<>("Wrong credentials",
				HttpStatus.UNAUTHORIZED);
		ResponseEntity<?> actualResponse = userService.authenticate(authRequest);
		assertEquals(expectedResponse, actualResponse);
	}
	@Test
	void testAuthenticate() {
		AuthRequest authRequest = new AuthRequest("test", "test");
		when(userRepository.findUserByLogin(any(String.class))).thenReturn(Optional.of(new User("test",
				passwordEncoder.encode("test"))));
		ResponseEntity<?> expectedResponse = new ResponseEntity<>(
				Collections.singletonMap("token", any(String.class)),
				HttpStatus.OK);
		ResponseEntity<?> actualResponse = userService.authenticate(authRequest);
		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	void testCreateUserFail(){
		User user = new User();
		ResponseEntity<?> expectedResponse = new ResponseEntity<>("Login cannot be empty",
				HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actualResponse = userService.createUser(user);
		assertEquals(expectedResponse, actualResponse);

		user.setLogin("test");
		expectedResponse = new ResponseEntity<>("Password cannot be empty",
				HttpStatus.BAD_REQUEST);
		actualResponse = userService.createUser(user);
		assertEquals(expectedResponse, actualResponse);

		user.setPassword("test");
		when(userRepository.findUserByLogin(any(String.class))).thenReturn(
				Optional.of(new User("test", "test"))
		);
		expectedResponse = new ResponseEntity<>("User with such login already exists",
				HttpStatus.BAD_REQUEST);
		actualResponse = userService.createUser(user);
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void testCreateUser() {
		User user = new User("test", "password");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>("User was successfully created",
				HttpStatus.CREATED);
		ResponseEntity<String> actualResponse = userService.createUser(user);

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
		assertEquals(expectedResponse.getBody(), actualResponse.getBody());
	}

	@Test
	void testDeleteNonExistingUser(){
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "test";
			}
		};
		ResponseEntity<?> expectedResponse = new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
 		ResponseEntity<?> actualResponse = userService.deleteUser(principal);
		 assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void testDeleteUser(){
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "test";
			}
		};
		when(userRepository.findUserByLogin(any(String.class))).thenReturn(
				Optional.of(new User("test", "test"))
		);
		ResponseEntity<?> expectedResponse = new ResponseEntity<>("User was successfully deleted",
				HttpStatus.NO_CONTENT);
		ResponseEntity<?> actualResponse = userService.deleteUser(principal);
		assertEquals(expectedResponse, actualResponse);
	}
}
