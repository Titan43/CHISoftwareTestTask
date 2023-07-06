package com.chiSoftware.testTask.controllers;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.services.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/contacts")
public class ContactController {
    @Autowired
    private final ContactService contactService;

    //According to a RESTful API convention POST method should be used for creating new resources, not PUT
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createContact(@RequestPart("contact") Contact contact,
                                                @RequestPart("image") MultipartFile image,
                                                Principal principal) {
        return contactService.createContact(contact, image, principal);
    }

    @GetMapping
    public ResponseEntity<?> getContacts(Principal principal) {
        return contactService.getContacts(principal);
    }

    @DeleteMapping(path = "/{name}")
    public ResponseEntity<String> deleteContact(@PathVariable String name, Principal principal) {
        return contactService.deleteContact(name, principal);
    }

    @PutMapping(path = "/{name}")
    public ResponseEntity<String> editContact(@PathVariable String name, @RequestPart("image") MultipartFile image,
                                              @RequestPart("contact") Contact contact,
                                              Principal principal) {
        return contactService.editContact(name, contact, image, principal);
    }
}
