package com.example.CitizensV1.Model;

import com.example.CitizensV1.Model.Citizens.CitizenSkill;
import com.example.CitizensV1.Model.Occupations.OccupationSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
// This class represents a skill that a citizen can have or an occupation may require.
// It is used in many-to-many relationships (through join entities like CitizenSkill and OccupationSkill)
// to associate skills with citizens and occupations, while allowing us to store additional data like skill level.
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String title;


    @OneToMany(mappedBy = "skill")
    @JsonIgnore
    private List<CitizenSkill> citizens = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    @JsonIgnore
    private List<OccupationSkill> requiredByOccupations = new ArrayList<>();

    @ManyToMany(mappedBy = "offeredSkills")
    //@JsonBackReference
    @JsonIgnore
    private List<Institute> institutes = new ArrayList<>();

    public Skill() {}

    public Skill(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CitizenSkill> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<CitizenSkill> citizens) {
        this.citizens = citizens;
    }

    public List<OccupationSkill> getRequiredByOccupations() {
        return requiredByOccupations;
    }

    public void setRequiredByOccupations(List<OccupationSkill> requiredByOccupations) {
        this.requiredByOccupations = requiredByOccupations;
    }

    public List<Institute> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(List<Institute> institutes) {
        this.institutes = institutes;
    }
}
