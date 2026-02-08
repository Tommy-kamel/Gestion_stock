package com.example.backend.repository;

import com.example.backend.entity.SortieLotDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SortieLotDetailRepository extends JpaRepository<SortieLotDetail, Long> {
}
