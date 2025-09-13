package com.example.CitizensV1.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "institutes")
public class Institute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String website;
    private String address;
    private String country;
    private String region;
    private String providerType; // e.g. "University", "Vocational", "Online", etc.
    //private int rating;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "institute_skills",
            joinColumns = @JoinColumn(name = "institute_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )

    @JsonIgnoreProperties("institutes")
    private List<Skill> offeredSkills = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public List<Skill> getOfferedSkills() {
        return offeredSkills;
    }

    public void setOfferedSkills(List<Skill> offeredSkills) {
        this.offeredSkills = offeredSkills;
    }
}
