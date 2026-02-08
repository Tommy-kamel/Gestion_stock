package com.example.backend.repository;

import com.example.backend.entity.BonCommandeVenteDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonCommandeVenteDetailsRepository extends JpaRepository<BonCommandeVenteDetails, Long> {
    List<BonCommandeVenteDetails> findByBonCommandeVenteId(Long bonCommandeVenteId);
}
