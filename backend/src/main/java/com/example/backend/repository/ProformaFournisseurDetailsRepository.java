package com.example.backend.repository;

import com.example.backend.entity.ProformaFournisseurDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProformaFournisseurDetailsRepository extends JpaRepository<ProformaFournisseurDetails, Long> {
}
