package com.example.CitizensV1.DTO.Skills;

// DTO class representing a missing skill for a citizen
// This is used as the response object returned by the /missingSkills endpoint
public class MissingSkillDTO {

    // Name of the missing skill
    private String skill;

    // Importance or weight of the skill
    private Double value;

    // The Pillar that the skill belongs to
    private String pillar;

    // The esco id of a skill
    private String skillId;

    // Constructor initializes the skill and value
    // If the value is null, default to 0.5
    public MissingSkillDTO(String skill, Double value, String pillar, String skillId) {
        this.skill = skill;
        this.value = value;
        this.pillar = pillar;
        this.skillId = skillId;
    }

    // Getter for the skill name
    public String getSkill() {
        return skill;
    }

    // Getter for the skill value
    public Double getValue() {
        return value;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public String getSkillId() {
        return skillId;
    }
    public void setSkillId(String skillid) {
        skillId = skillid;
    }
}
