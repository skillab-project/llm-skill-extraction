package com.example.CitizensV1.Repository;

import com.example.CitizensV1.Model.Citizens.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//-------------------------------CITIZENS--------------------------------
// Repository interface for managing Citizen entities in the database
public interface CitizensRepository extends JpaRepository<Citizen, Long> {

    // Custom query method to find a citizen by their email address
    Optional<Citizen> findByEmail(String email);
}
