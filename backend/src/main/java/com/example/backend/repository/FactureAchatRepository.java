package com.example.backend.repository;

import com.example.backend.entity.FactureAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureAchatRepository extends JpaRepository<FactureAchat, Long> {
}
