package com.example.backend.repository;

import com.example.backend.entity.DemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeAchatRepository extends JpaRepository<DemandeAchat, Long> {
    // Additional custom query methods can be added here if needed
}