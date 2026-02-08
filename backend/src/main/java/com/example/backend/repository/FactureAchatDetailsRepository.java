package com.example.backend.repository;

import com.example.backend.entity.FactureAchatDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureAchatDetailsRepository extends JpaRepository<FactureAchatDetails, Long> {
}
