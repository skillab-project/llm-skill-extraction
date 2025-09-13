package com.example.CitizensV1.Model.Occupations;

import com.example.CitizensV1.Model.Skill;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
// This class represents a join entity between Occupation and Skill.
// It defines which skills are required for a specific occupation, including the required level.
// We use this instead of a direct @ManyToMany relationship in order to store extra attributes (like "level").
public class OccupationSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "occupation_id")
    @JsonBackReference
    private Occupation occupation;

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

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
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

