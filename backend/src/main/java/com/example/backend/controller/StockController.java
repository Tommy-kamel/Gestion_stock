package com.example.backend.controller;

import com.example.backend.entity.Article;
import com.example.backend.entity.Depot;
import com.example.backend.repository.ArticleRepository;
import com.example.backend.repository.DepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final ArticleRepository articleRepository;
    private final DepotRepository depotRepository;

    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getArticles() {
        try {
            List<Article> articles = articleRepository.findAll();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/depots")
    public ResponseEntity<List<Depot>> getDepots() {
        try {
            List<Depot> depots = depotRepository.findAll();
            return ResponseEntity.ok(depots);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}