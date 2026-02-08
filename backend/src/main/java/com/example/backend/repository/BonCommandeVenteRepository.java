package com.example.backend.repository;

import com.example.backend.entity.BonCommandeVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonCommandeVenteRepository extends JpaRepository<BonCommandeVente, Long> {
    List<BonCommandeVente> findByProformaVenteId(Long proformaVenteId);
    BonCommandeVente findByNumeroBc(String numeroBc);
}
