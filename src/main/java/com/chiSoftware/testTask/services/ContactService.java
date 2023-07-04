package com.chiSoftware.testTask.services;

import com.chiSoftware.testTask.entities.contact.Contact;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface ContactService {
    ResponseEntity<String> createContact(Contact contact, Principal principal);
    ResponseEntity<?> getContacts(Principal principal);
    ResponseEntity<String> deleteContact(String name, Principal principal);
    ResponseEntity<String> editContact(Contact contact, Principal principal);
}
