package com.example.backend.repository;

import com.example.backend.entity.ProformaFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaFournisseurRepository extends JpaRepository<ProformaFournisseur, Long> {
    List<ProformaFournisseur> findByDemandeAchatId(Long demandeAchatId);
    List<ProformaFournisseur> findByStatusCode(String statusCode);
}
