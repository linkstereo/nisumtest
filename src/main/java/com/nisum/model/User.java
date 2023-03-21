package com.nisum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "USER")
@Data
public class User {

    @Id
    @GeneratedValue
    private UUID id;
    private Date created;
    private Date modified;
    private Date lastLogin;
    private String email;
    private String name;
    private String password;
    private boolean isActive;

    @OneToMany(mappedBy = "user")
    private Set<Phone> phones;
}

