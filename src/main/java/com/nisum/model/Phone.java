package com.nisum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "PHONE")
@Data
@EqualsAndHashCode
public class Phone {

    @Id
    private String number;
    private String cityCode;
    private String countryCode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id", nullable=false)
    @EqualsAndHashCode.Exclude
    private User user;
}
