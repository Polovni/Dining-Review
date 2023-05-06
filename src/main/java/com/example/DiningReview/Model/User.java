package com.example.DiningReview.Model;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "`USER`")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String displayName;
    private String city;
    private String state;
    private String zipCode;
    private Boolean peanut;
    private Boolean dairy;
    private Boolean egg;
}
