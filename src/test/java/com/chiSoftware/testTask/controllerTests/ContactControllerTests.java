package com.chiSoftware.testTask.controllerTests;

import com.chiSoftware.testTask.controllers.ContactController;
import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.security.JwtUtil;
import com.chiSoftware.testTask.services.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

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
    private ContactController contactController;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "test")
    void testCreateContact() throws Exception{
        when(contactService.createContact(any(Contact.class), any(MultipartFile.class), any(Principal.class)))
                .thenReturn(new ResponseEntity<>(
                        "Contact was successfully created. Image was added",
                        HttpStatus.CREATED));

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/contacts");
        builder.header("Authorization", "Bearer ");
        builder.contentType(MediaType.APPLICATION_JSON);

        byte[] imageBytes = {0x00, 0x01, 0x02};
        MockMultipartFile image = new MockMultipartFile("image", "text.jpg",
                "image/jpeg", imageBytes);
        MockMultipartFile contactJson = new MockMultipartFile("contact", "contact.json",
                "application/json", objectMapper.writeValueAsString(new Contact()).getBytes());
        builder.file(image);
        builder.file(contactJson);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Contact was successfully created. Image was added")
                );
    }

    @Test
    @WithMockUser(username = "test")
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

    @Test
    @WithMockUser(username = "test")
    void testDeleteContact() throws Exception{
        when(contactService.deleteContact(any(String.class), any(Principal.class)))
                .thenReturn(new ResponseEntity<>("Contact deleted successfully", HttpStatus.NO_CONTENT));
        mockMvc.perform(MockMvcRequestBuilders.delete("/contacts/"+"test")
                        .header("Authorization", "Bearer ")
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string("Contact deleted successfully"));
    }

    @Test
    @WithMockUser(username = "test")
    void testEditContact() throws Exception{
        when(contactService.editContact(any(String.class), any(Contact.class), any(MultipartFile.class),
                any(Principal.class)))
                .thenReturn(new ResponseEntity<>("Contact updated successfully", HttpStatus.OK));

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/contacts/test");
        builder.header("Authorization", "Bearer ");
        builder.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        byte[] imageBytes = {0x00, 0x01, 0x02};
        MockMultipartFile image = new MockMultipartFile("image", "text.jpg",
                "image/jpeg", imageBytes);
        MockMultipartFile contactJson = new MockMultipartFile("contact", "contact.json",
                "application/json", objectMapper.writeValueAsString(new Contact()).getBytes());

        builder.file(image);
        builder.file(contactJson);

        mockMvc.perform(
                       builder
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact updated successfully"));
    }
}