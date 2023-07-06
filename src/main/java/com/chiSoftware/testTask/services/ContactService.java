package com.chiSoftware.testTask.services;

import com.chiSoftware.testTask.entities.contact.Contact;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface ContactService {
    ResponseEntity<String> createContact(Contact contact, MultipartFile image, Principal principal);
    ResponseEntity<?> getContacts(Principal principal);
    ResponseEntity<String> deleteContact(String name, Principal principal);
    ResponseEntity<String> editContact(String name, Contact contact, MultipartFile image, Principal principal);
}
