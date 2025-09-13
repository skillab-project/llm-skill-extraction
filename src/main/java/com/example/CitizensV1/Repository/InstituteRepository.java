package com.example.CitizensV1.Repository;

import com.example.CitizensV1.Model.Institute;
import com.example.CitizensV1.Model.Occupations.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//-------------------------------INSTITUTES--------------------------------
// Repository interface for managing Institute entities in the database
public interface InstituteRepository extends JpaRepository<Institute, Long> {

    // Custom query method to find an institute by its name
    Optional<Institute> findByName(String instituteName);
}
