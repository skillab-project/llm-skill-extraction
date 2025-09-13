package com.example.CitizensV1.Model.Citizens;

import com.example.CitizensV1.Model.Skill;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
// This class serves as a join entity in a many-to-many relationship between Citizen and Skill.
// It allows us to represent additional information about the relationship, such as the skill level
// a citizen has for a specific skill. Instead of using a direct @ManyToMany mapping, this entity
// gives us more flexibility and makes it possible to store attributes like "level" per skill.
public class CitizenSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "citizen_id")
    @JsonBackReference
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

