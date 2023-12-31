package com.chiSoftware.testTask.entities.contact;

import com.chiSoftware.testTask.entities.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ElementCollection
    private String[] phones;
    @ElementCollection
    private String[] emails;
    @Lob
    @Column(columnDefinition = "mediumblob")
    private byte[] image;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Contact(String name, String[] phones, String[] emails, User user) {
        this.name = name;
        this.phones = phones;
        this.emails = emails;
        this.user = user;
    }
}
