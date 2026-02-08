package com.example.backend.repository;

import com.example.backend.entity.CaisseMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaisseMouvementRepository extends JpaRepository<CaisseMouvement, Long> {
}
