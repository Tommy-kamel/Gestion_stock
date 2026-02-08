package com.example.backend.repository;

import com.example.backend.entity.ProformaVenteDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaVenteDetailsRepository extends JpaRepository<ProformaVenteDetails, Long> {
    List<ProformaVenteDetails> findByProformaVenteId(Long proformaVenteId);
}
