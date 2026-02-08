package com.example.backend.repository;

import com.example.backend.entity.LotStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotStockRepository extends JpaRepository<LotStock, Long> {
}
