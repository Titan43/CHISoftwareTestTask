package com.chiSoftware.testTask.entities.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c JOIN c.emails e WHERE e IN :emails")
    List<Contact> findContactsByEmailIn(String[] emails);
    @Query("SELECT c FROM Contact c JOIN c.phones e WHERE e IN :phones")
    List<Contact> findContactsByPhoneIn(String[] phones);
    Optional<Contact> findContactByName(String name);
}
