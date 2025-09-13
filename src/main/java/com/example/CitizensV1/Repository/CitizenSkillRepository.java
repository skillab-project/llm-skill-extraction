package com.example.CitizensV1.Repository;

import com.example.CitizensV1.Model.Citizens.CitizenSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//-------------------------------CITIZEN SKILLS--------------------------------
public interface CitizenSkillRepository extends JpaRepository<CitizenSkill, Long> {

    // Finds all skills for a given citizen by citizen ID
    List<CitizenSkill> findByCitizenId(Long citizenId);
}
