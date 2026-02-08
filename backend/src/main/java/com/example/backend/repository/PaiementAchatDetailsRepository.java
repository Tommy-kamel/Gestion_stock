package com.example.backend.repository;

import com.example.backend.entity.PaiementAchatDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementAchatDetailsRepository extends JpaRepository<PaiementAchatDetails, Long> {
}
