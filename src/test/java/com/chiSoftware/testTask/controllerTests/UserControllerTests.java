package com.chiSoftware.testTask.controllerTests;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.AuthRequest;
import com.chiSoftware.testTask.security.JwtUtil;
import com.chiSoftware.testTask.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;

import static com.chiSoftware.testTask.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

	@MockBean
	private UserService userService;

	@MockBean
	private JwtUtil jwtUtil;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testAuthenticateEndpoint() throws Exception {
		AuthRequest authRequest = new AuthRequest("testuser", "testpassword");

		when(userService.authenticate(authRequest)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(MockMvcRequestBuilders.post("/auth").servletPath(AUTH_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequest)))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	void testRegisterEndpoint() throws Exception {
		User user = new User("testuser", "testpassword");

		when(userService.createUser(user)).thenReturn(
				new ResponseEntity<>("User was successfully created", HttpStatus.CREATED));

		mockMvc.perform(MockMvcRequestBuilders.post("/register").servletPath(REGISTER_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().string("User was successfully created"));
	}

	@Test
	void testDeleteEndpointUnauthorized() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/delete"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().string("JWT Token is missing"));
	}

	@Test
	@WithMockUser(username = "test")
	void testDeleteEndpoint() throws Exception {
		when(userService.deleteUser(any(Principal.class)))
				.thenReturn(new ResponseEntity<>("User was successfully deleted",
						HttpStatus.NO_CONTENT));
		mockMvc.perform(MockMvcRequestBuilders.delete("/delete").header("Authorization",
						"Bearer "))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andExpect(MockMvcResultMatchers.content().string("User was successfully deleted"));
	}
}

