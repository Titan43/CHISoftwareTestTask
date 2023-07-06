package com.chiSoftware.testTask.serviceTests;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.entities.contact.ContactRepository;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.services.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.chiSoftware.testTask.Constants.LINK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactServiceTests {
    @MockBean
    private ContactRepository contactRepository;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ContactService contactService;

    private final Principal principal = new Principal() {
        @Override
        public String getName() {
            return "test";
        }
    };

    @Test
    void testCreateContactFail(){
        Contact contact = new Contact();
        ResponseEntity<String> expected = new ResponseEntity<>("Contact name cannot be empty",
                HttpStatus.BAD_REQUEST);
        ResponseEntity<String> actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);

        contact.setName("test");
        when(contactRepository.findContactByName(eq("test"), any(String.class))).thenReturn(
                Optional.of(new Contact())
        );
        expected = new ResponseEntity<>("Contact with such name already exists",
                HttpStatus.BAD_REQUEST);
        actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);

        contact.setName("test2");
        expected = new ResponseEntity<>("Invalid email entered or contact with such email already exists",
                HttpStatus.BAD_REQUEST);
        actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);

        contact.setEmails(new String[0]);
        expected = new ResponseEntity<>(
                "Invalid phoneNumber entered or contact with such phoneNumber already exists",
                HttpStatus.BAD_REQUEST);
        actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);

        contact.setPhones(new String[0]);
        expected = new ResponseEntity<>(
                "Owner of this token does not exist",
                HttpStatus.UNAUTHORIZED);
        actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);
    }

    @Test
    void testCreateContact(){
        Contact contact = new Contact("test", new String[0], new String[0], new User());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromPath(LINK+"/contacts/test").build().toUri());
        when(userRepository.findUserByLogin(any(String.class))).thenReturn(
                Optional.of(new User("test", "test"))
        );

        ResponseEntity<String> expected = new ResponseEntity<>("Contact was successfully created",
                headers,
                HttpStatus.CREATED);
        ResponseEntity<String> actual = contactService.createContact(contact, principal);
        assertEquals(expected, actual);
    }

    @Test
    void testGetContactsEmpty(){
        ResponseEntity<?> expected = new ResponseEntity<>("No contacts to show",
                HttpStatus.NO_CONTENT);
        ResponseEntity<?> actual = contactService.getContacts(principal);
        assertEquals(expected, actual);
    }

    @Test
    void testGetContacts(){
        List<Contact> contacts = Arrays.asList(new Contact(), new Contact());
        when(contactRepository.findContactsByOwner(principal.getName()))
                .thenReturn(contacts);

        ResponseEntity<?> expected = new ResponseEntity<>(contacts,
                HttpStatus.OK);
        ResponseEntity<?> actual = contactService.getContacts(principal);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteContactFail(){
        ResponseEntity<String> expected = new ResponseEntity<>("Name cannot be empty",
                HttpStatus.BAD_REQUEST);
        ResponseEntity<String> actual = contactService.deleteContact("", principal);
        assertEquals(expected, actual);

        expected = new ResponseEntity<>("Contact with such name does not exist",
                HttpStatus.BAD_REQUEST);
        actual = contactService.deleteContact("test", principal);
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteContact(){
        when(contactRepository.findContactByName(eq("test"), any(String.class))).thenReturn(
                Optional.of(new Contact())
        );

        ResponseEntity<String> expected = new ResponseEntity<>("Contact deleted successfully",
                HttpStatus.NO_CONTENT);
        ResponseEntity<String> actual = contactService.deleteContact("test", principal);
        assertEquals(expected, actual);
    }

    @Test
    void testEditContactFail(){
        Contact contact = new Contact();
        ResponseEntity<String> expected = new ResponseEntity<>("Name variable cannot be empty",
                HttpStatus.BAD_REQUEST);
        ResponseEntity<String> actual = contactService.editContact("", contact, principal);
        assertEquals(expected, actual);

        contact.setId(1L);
        expected = new ResponseEntity<>("Contact id cannot be changed", HttpStatus.BAD_REQUEST);
        actual = contactService.editContact("test", contact, principal);
        assertEquals(expected, actual);

        contact.setId(null);
        expected = new ResponseEntity<>("Contact name cannot be empty", HttpStatus.BAD_REQUEST);
        actual = contactService.editContact("test", contact, principal);
        assertEquals(expected, actual);

        when(contactRepository.findContactByName(eq("test"), any(String.class))).thenReturn(
                Optional.of(new Contact())
        );
        contact.setName("test");
        expected = new ResponseEntity<>("Contact with such name already exists", HttpStatus.BAD_REQUEST);
        actual = contactService.editContact("test2", contact, principal);
        assertEquals(expected, actual);

        contact.setEmails(new String[]{"test", "test"});
        expected = new ResponseEntity<>("Invalid email entered or contact with such email already exists",
                HttpStatus.BAD_REQUEST);
        actual = contactService.editContact("test", contact, principal);
        assertEquals(expected, actual);

        contact.setEmails(new String[]{"test@gmail.com", "test@yahoo.com"});
        contact.setPhones(new String[]{"3442", "223423"});
        expected = new ResponseEntity<>(
                "Invalid phoneNumber entered or contact with such phoneNumber already exists",
                HttpStatus.BAD_REQUEST);
        actual = contactService.editContact("test", contact, principal);
        assertEquals(expected, actual);
    }

    @Test
    void testEditContact(){
        Contact contact = new Contact("test", new String[]{"+380972215511", "+380972255112"},
                new String[]{"t@gm.com", "m@gm.com"}, new User());
        when(contactRepository.findContactByName(eq("test"), any(String.class))).thenReturn(
                Optional.of(new Contact())
        );
        ResponseEntity<String> expected = new ResponseEntity<>("Contact updated successfully",
                HttpStatus.OK);
        ResponseEntity<String> actual = contactService.editContact("test", contact, principal);
        assertEquals(expected, actual);
    }
}
