package com.example.backend.repository;

import com.example.backend.entity.Article;
import com.example.backend.entity.Depot;
import com.example.backend.entity.LotStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LotStockRepository extends JpaRepository<LotStock, Long> {
    List<LotStock> findByArticleAndDepotAndQuantiteRestanteGreaterThan(Article article, Depot depot, BigDecimal quantite);
}
