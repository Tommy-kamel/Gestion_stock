package com.example.backend.repository;

import com.example.backend.entity.ProformaVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaVenteRepository extends JpaRepository<ProformaVente, Long> {
    List<ProformaVente> findByDemandeAchatClientId(Long demandeAchatClientId);
    ProformaVente findByNumeroProforma(String numeroProforma);
}
