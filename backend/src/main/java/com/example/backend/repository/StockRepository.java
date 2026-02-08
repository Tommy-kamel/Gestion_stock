package com.example.backend.repository;

import com.example.backend.entity.Stock;
import com.example.backend.entity.Article;
import com.example.backend.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByArticleAndDepot(Article article, Depot depot);
}
