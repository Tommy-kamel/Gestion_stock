package com.example.backend.repository;

import com.example.backend.entity.DemandeAchatClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeAchatClientDetailsRepository extends JpaRepository<DemandeAchatClientDetails, Long> {
    List<DemandeAchatClientDetails> findByDemandeAchatClientId(Long demandeAchatClientId);
}
