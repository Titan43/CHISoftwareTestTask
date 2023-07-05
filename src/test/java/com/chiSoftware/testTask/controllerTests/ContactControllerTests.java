package com.chiSoftware.testTask.controllerTests;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.JwtUtil;
import com.chiSoftware.testTask.services.ContactService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTests {
    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "andy")
    void testCreateContact() throws Exception{
        when(contactService.createContact(any(Contact.class), any(Principal.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/contacts")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Contact(
                                        "test", new String[0], new String[0], new User()
                                ))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "andy")
    void testGetContacts() throws Exception{
        List<Contact> contacts = Arrays.asList(
                new Contact(), new Contact()
        );
        when(contactService.getContacts(any(Principal.class)))
                .thenReturn(new ResponseEntity(contacts, HttpStatus.OK));
        mockMvc.perform(MockMvcRequestBuilders.get("/contacts")
                        .header("Authorization", "Bearer ")
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper
                        .writeValueAsString(contacts)));
    }
}