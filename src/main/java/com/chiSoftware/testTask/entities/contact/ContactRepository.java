package com.chiSoftware.testTask.entities.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c JOIN c.emails e WHERE e IN :emails AND c.user.login = :login")
    List<Contact> findContactsByEmailIn(@Param("emails") String[] emails, @Param("login") String login);
    @Query("SELECT c FROM Contact c JOIN c.phones p WHERE p IN :phones AND c.user.login = :login")
    List<Contact> findContactsByPhoneIn(@Param("phones") String[] phones, @Param("login") String login);
    @Query("SELECT c FROM Contact c WHERE c.name = ?1 AND c.user.login = ?2")
    Optional<Contact> findContactByName(String name, String login);
    @Query("SELECT c FROM Contact c WHERE c.user.login = ?1")
    List<Contact> findContactsByOwner(String login);
}
