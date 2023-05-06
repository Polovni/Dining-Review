package com.example.DiningReview.Model;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "RESTAURANT")
public class Restaurant {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String line1;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String website;
    private String overallScore;
    private String peanutScore;
    private String dairyScore;
    private String eggScore;
}

