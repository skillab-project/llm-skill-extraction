package com.example.CitizensV1.DTO.Skills;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO class representing a skill required for a specific role
// This class is used to map JSON responses from the external API into Java objects
public class RequiredSkillResponse {

    // Maps the JSON property "Role" to this field
    @JsonProperty("Role")
    private String Role;

    // Maps the JSON property "Skill" to this field
    @JsonProperty("Skill")
    private String Skill;

    // Maps the JSON property "Pillar" to this field
    @JsonProperty("Pillar")
    private String Pillar;

    // Maps the JSON property "Value" to this field
    // Represents the importance or weight of the skill
    @JsonProperty("Value")
    private Double Value;

    // Maps the JSON property "SkillId" to this field
    // Represents a unique identifier for the skill
    @JsonProperty("SkillId")
    private String SkillId;


    // Getter for Role
    public String getRole() {
        return Role;
    }

    // Setter for Role
    public void setRole(String role) {
        Role = role;
    }

    // Getter for Skill
    public String getSkill() {
        return Skill;
    }

    // Setter for Skill
    public void setSkill(String skill) {
        Skill = skill;
    }

    // Getter for Pillar
    public String getPillar() {
        return Pillar;
    }

    // Setter for Pillar
    public void setPillar(String pillar) {
        Pillar = pillar;
    }

    // Getter for Value
    public Double getValue() {
        return Value;
    }

    // Setter for Value
    // If null is passed, default to 0.5 to avoid null issues
    public void setValue(Double value) {
        Value = value;
    }

    // Getter for SkillId
    public String getSkillId() {
        return SkillId;
    }

    // Setter for SkillId
    public void setSkillId(String skillId) {
        SkillId = skillId;
    }
}
