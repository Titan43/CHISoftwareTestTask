package com.chiSoftware.testTask.serviceProviders;

import com.chiSoftware.testTask.entities.contact.Contact;
import com.chiSoftware.testTask.entities.contact.ContactRepository;
import com.chiSoftware.testTask.entities.user.User;
import com.chiSoftware.testTask.entities.user.UserRepository;
import com.chiSoftware.testTask.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.chiSoftware.testTask.Constants.*;

@Service
@AllArgsConstructor
public class ContactServiceProvider implements ContactService {
    @Autowired
    private final ContactRepository contactRepository;
    @Autowired
    private final UserRepository userRepository;

    private final Pattern phoneNumberPattern = Pattern.compile(PHONE_NUMBER_REGEX, Pattern.CASE_INSENSITIVE);
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    private boolean emailsAreInvalid(String[] emails) {
        List<Contact> contacts = contactRepository.findContactsByEmailIn(emails);
        for (String email : emails) {
            if (email == null || !emailPattern.matcher(email).matches() || contactExistsWithEmail(contacts, email)) {
                return true;
            }
        }
        return false;
    }

    private boolean contactExistsWithEmail(List<Contact> contacts, String email) {
        for (Contact contact : contacts) {
            if (Arrays.asList(contact.getEmails()).contains(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean phoneNumbersAreInvalid(String[] phoneNumbers) {
        List<Contact> contacts = contactRepository.findContactsByPhoneIn(phoneNumbers);
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber == null || !phoneNumberPattern.matcher(phoneNumber).matches()
                    || contactExistsWithPhoneNumber(contacts, phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean contactExistsWithPhoneNumber(List<Contact> contacts, String phoneNumber) {
        for (Contact contact : contacts) {
            if (Arrays.asList(contact.getPhones()).contains(phoneNumber)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public ResponseEntity<String> createContact(Contact contact, Principal principal) {
        if(contact.getName() == null || contact.getName().strip().equals("")){
            return new ResponseEntity<>("Contact name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(contactRepository.findContactByName(contact.getName(), principal.getName()).isPresent()){
            return new ResponseEntity<>("Contact with such name already exists",
                    HttpStatus.BAD_REQUEST);
        }
        else if(contact.getEmails()==null || emailsAreInvalid(contact.getEmails())){
            return new ResponseEntity<>("Invalid email entered or contact with such email already exists",
                    HttpStatus.BAD_REQUEST);
        }
        else if(contact.getPhones()==null || phoneNumbersAreInvalid(contact.getPhones())){
            return new ResponseEntity<>("Invalid phoneNumber entered or contact with such phoneNumber already exists",
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = userRepository.findUserByLogin(principal.getName());
        if(optionalUser.isEmpty()){
            return new ResponseEntity<>("Owner of this token does not exist", HttpStatus.UNAUTHORIZED);
        }
        contact.setUser(optionalUser.get());
        contactRepository.save(contact);
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK+"/contact/"+contact.getName())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("Contact was successfully created", headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getContacts(Principal principal) {
        List<Contact> contacts = contactRepository.findContactsByOwner(principal.getName());
        if(contacts.size()==0){
            return new ResponseEntity<>("No contacts to show", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(contacts, HttpStatus.OK);
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
