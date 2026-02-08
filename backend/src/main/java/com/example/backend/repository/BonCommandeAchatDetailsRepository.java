package com.example.backend.repository;

import com.example.backend.entity.BonCommandeAchatDetails;
import com.example.backend.entity.BonCommandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonCommandeAchatDetailsRepository extends JpaRepository<BonCommandeAchatDetails, Long> {
    List<BonCommandeAchatDetails> findByBonCommandeAchat(BonCommandeAchat bonCommandeAchat);
}
