package com.chiSoftware.testTask.serviceProviders;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ContactServiceProvider implements ContactService {
    @Override
    public ResponseEntity<String> createContact(Contact contact, Principal principal) {
        return null;
    }

    @Override
    public ResponseEntity<?> getContacts(Principal principal) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteContact(String name, Principal principal) {
        return null;
    }

    @Override
    public ResponseEntity<String> editContact(Contact contact, Principal principal) {
        return null;
    }
}
