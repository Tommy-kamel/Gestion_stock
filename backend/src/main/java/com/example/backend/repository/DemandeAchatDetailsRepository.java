package com.example.backend.repository;

import com.example.backend.entity.DemandeAchatDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeAchatDetailsRepository extends JpaRepository<DemandeAchatDetails, Long> {
    // Additional custom query methods can be added here if needed
}