package com.example.CitizensV1.Model.Occupations;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "occupations")
public class Occupation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ή άλλο strategy που χρησιμοποιείς
    private Long id;


    @Column(unique = true)
    private String name; // ex: "Java Developer", "Data Analyst"

    private String sector; // ex: "IT", "Healthcare", "Education"

    private String description; // description of the role

    @OneToMany(mappedBy = "occupation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OccupationSkill> requiredSkills = new ArrayList<>();

    public Occupation() {
    }

    public Occupation(String name, String sector, String description, List<OccupationSkill> requiredSkills) {
        this.name = name;
        this.sector = sector;
        this.description = description;
        this.requiredSkills = requiredSkills;
    }

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

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OccupationSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<OccupationSkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    @Override
    public String toString() {
        return "Occupation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", description='" + description + '\'' +
                ", requiredSkills=" + requiredSkills +
                '}';
    }
}
