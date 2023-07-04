package com.chiSoftware.testTask.controllers;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/contacts")
public class ContactController {
    @Autowired
    private final ContactService contactService;

    //According to a RESTful API convention POST method should be used for creating new resources, not PUT
    @PostMapping
    public ResponseEntity<String> createContact(@RequestBody Contact contact, Principal principal) {
        return contactService.createContact(contact, principal);
    }

    @GetMapping
    public ResponseEntity<?> getContacts(Principal principal) {
        return contactService.getContacts(principal);
    }

    @DeleteMapping(path = "/{name}")
    public ResponseEntity<String> deleteContact(@PathVariable String name, Principal principal) {
        return contactService.deleteContact(name, principal);
    }

    @PutMapping
    public ResponseEntity<String> editContact(@RequestBody Contact contact, Principal principal) {
        return contactService.editContact(contact, principal);
    }
}
