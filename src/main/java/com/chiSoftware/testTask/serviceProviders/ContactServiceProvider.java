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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;

import static com.chiSoftware.testTask.Constants.*;

@Service
@AllArgsConstructor
public class ContactServiceProvider implements ContactService {
    @Autowired
    private final ContactRepository contactRepository;
    @Autowired
    private final UserRepository userRepository;

    private final Pattern phoneNumberPattern = Pattern.compile(PHONE_NUMBER_REGEX);
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    private String[] getUnique(String[] original, String[] updated){
        if(original == null){
            return updated;
        }
        Set<String> originalSet = new HashSet<>(Arrays.asList(original));
        Set<String> updatedSet = new HashSet<>(Arrays.asList(updated));
        updatedSet.removeAll(originalSet);
        return updatedSet.toArray(new String[0]);
    }

    private boolean emailsAreInvalid(String[] emails, String login) {
        List<Contact> contacts = contactRepository.findContactsByEmailIn(emails, login);
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

    private boolean phoneNumbersAreInvalid(String[] phoneNumbers, String login) {
        List<Contact> contacts = contactRepository.findContactsByPhoneIn(phoneNumbers, login);
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
    public ResponseEntity<String> createContact(Contact contact, MultipartFile image, Principal principal) {
        if(contact.getName() == null || contact.getName().strip().equals("")){
            return new ResponseEntity<>("Contact name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(contactRepository.findContactByName(contact.getName(), principal.getName()).isPresent()){
            return new ResponseEntity<>("Contact with such name already exists",
                    HttpStatus.BAD_REQUEST);
        }
        else if(contact.getEmails()==null || emailsAreInvalid(contact.getEmails(), principal.getName())){
            return new ResponseEntity<>("Invalid email entered or contact with such email already exists",
                    HttpStatus.BAD_REQUEST);
        }
        else if(contact.getPhones()==null || phoneNumbersAreInvalid(contact.getPhones(), principal.getName())){
            return new ResponseEntity<>("Invalid phoneNumber entered or contact with such phoneNumber already exists",
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = userRepository.findUserByLogin(principal.getName());
        if(optionalUser.isEmpty()){
            return new ResponseEntity<>("Owner of this token does not exist", HttpStatus.UNAUTHORIZED);
        }
        boolean imageWasAdded = true;
        if(image != null && !image.isEmpty()) {
            try {
                byte[] bytes = image.getBytes();
                contact.setImage(bytes);
            } catch (IOException e) {
                imageWasAdded = false;
            }
        }else{
            imageWasAdded = false;
        }
        contact.setUser(optionalUser.get());
        contactRepository.save(contact);
        URI location = ServletUriComponentsBuilder
                .fromPath(LINK+"/contacts/"+contact.getName())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>("Contact was successfully created. "+
                (imageWasAdded? "Image was added": "Image was not added"),
                headers, HttpStatus.CREATED);
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
        if(name==null || name.strip().equals("")){
            return new ResponseEntity<>("Name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        Optional<Contact> contact = contactRepository.findContactByName(name, principal.getName());
        if(contact.isEmpty()){
            return new ResponseEntity<>("Contact with such name does not exist", HttpStatus.NOT_FOUND);
        }
        contactRepository.delete(contact.get());
        return new ResponseEntity<>("Contact deleted successfully", HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<String> editContact(String name, Contact contact, MultipartFile image,
                                              Principal principal) {
        if(name == null || name.strip().equals("")){
            return new ResponseEntity<>("Name variable cannot be empty",
                    HttpStatus.BAD_REQUEST);
        }
        else if(contact.getId()!=null){
            return new ResponseEntity<>("Contact id cannot be changed", HttpStatus.BAD_REQUEST);
        }
        else if(contact.getName() == null || contact.getName().strip().equals("")){
            return new ResponseEntity<>("Contact name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else if(contactRepository.findContactByName(contact.getName(), principal.getName()).isPresent()
            && !contact.getName().equals(name)){
            return new ResponseEntity<>("Contact with such name already exists",
                    HttpStatus.BAD_REQUEST);
        }
        Optional<Contact> oldContact = contactRepository.findContactByName(name, principal.getName());
        if(oldContact.isEmpty()){
            return new ResponseEntity<>("Contact with such name does not exist",
                    HttpStatus.NOT_FOUND);
        }
        Contact oldContactData = oldContact.get();
        if(contact.getEmails()==null ||
                emailsAreInvalid(getUnique(oldContactData.getEmails(), contact.getEmails()), principal.getName())){
                return new ResponseEntity<>(
                        "Invalid email entered or contact with such email already exists",
                    HttpStatus.BAD_REQUEST);

        } else if(contact.getPhones()==null || phoneNumbersAreInvalid(
                getUnique(oldContactData.getPhones(), contact.getPhones()), principal.getName())){
                return new ResponseEntity<>(
                        "Invalid phoneNumber entered or contact with such phoneNumber already exists",
                    HttpStatus.BAD_REQUEST);
        }
        boolean imageWasAdded = true;
        if(image != null && !image.isEmpty()) {
            try {
                byte[] bytes = image.getBytes();
                contact.setImage(bytes);
            } catch (IOException e) {
                imageWasAdded = false;
            }
        }else{
            imageWasAdded = false;
        }
        oldContactData.setEmails(contact.getEmails());
        oldContactData.setPhones(contact.getPhones());
        oldContactData.setName(contact.getName());

        contactRepository.save(oldContactData);

        return new ResponseEntity<>("Contact updated successfully. "+
                (imageWasAdded? "Image was updated": "Image was not updated"), HttpStatus.OK);
    }
}
