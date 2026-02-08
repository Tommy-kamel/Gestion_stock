package com.example.backend.repository;

import com.example.backend.entity.DemandeAchatClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeAchatClientRepository extends JpaRepository<DemandeAchatClient, Long> {
    List<DemandeAchatClient> findByClientId(Long clientId);
    List<DemandeAchatClient> findByStatusId(Long statusId);
    DemandeAchatClient findByNumeroDa(String numeroDa);
}
