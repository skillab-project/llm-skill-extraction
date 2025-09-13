package com.example.CitizensV1.Repository;

import com.example.CitizensV1.Model.Occupations.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//-------------------------------OCCUPATIONS--------------------------------
// Repository interface for managing Occupation entities in the database
public interface OccupationsRepository extends JpaRepository<Occupation, Long> {

    // Custom query method to find an occupation by its name
    Optional<Occupation> findByName(String targetOccupationName);
}
