package com.example.backend.repository;

import com.example.backend.entity.BonCommandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonCommandeAchatRepository extends JpaRepository<BonCommandeAchat, Long> {
}
