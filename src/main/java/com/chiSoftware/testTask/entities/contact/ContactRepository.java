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
    @Query("SELECT c FROM Contact c WHERE c.name = ?1 AND c.user.login = ?2")
    Optional<Contact> findContactByName(String name, String login);
    @Query("SELECT c FROM Contact c WHERE c.user.login = ?1")
    List<Contact> findContactsByOwner(String login);
}
