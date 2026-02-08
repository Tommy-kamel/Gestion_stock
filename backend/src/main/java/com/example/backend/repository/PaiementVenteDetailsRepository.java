package com.example.backend.repository;

import com.example.backend.entity.PaiementVenteDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementVenteDetailsRepository extends JpaRepository<PaiementVenteDetails, Long> {
    List<PaiementVenteDetails> findByPaiementVenteId(Long paiementVenteId);
}
