package com.example.CitizensV1.Repository;

import com.example.CitizensV1.Model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//-------------------------------SKILLS--------------------------------
// Repository interface for performing database operations on Skill entities
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Custom method to find a Skill entity by its title
    Optional<Skill> findByTitle(String title);
}
