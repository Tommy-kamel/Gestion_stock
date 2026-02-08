package com.example.backend.repository;

import com.example.backend.entity.PaiementVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementVenteRepository extends JpaRepository<PaiementVente, Long> {
    List<PaiementVente> findByFactureVenteId(Long factureVenteId);
}
