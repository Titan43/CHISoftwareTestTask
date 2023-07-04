package com.chiSoftware.testTask.entities.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c JOIN c.emails e WHERE e = :email")
    Optional<Contact> findByEmail(String email);
    @Query("SELECT c FROM Contact c JOIN c.phoneNumbers e WHERE e = :phoneNumber")
    Optional<Contact> findByPhoneNumber(String phoneNumber);
}
