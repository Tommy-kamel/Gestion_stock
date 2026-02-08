package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final ArticleRepository articleRepository;
    private final DepotRepository depotRepository;
    private final StockRepository stockRepository;
    private final LotStockRepository lotStockRepository;

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

    @GetMapping("/valorisation")
    public ResponseEntity<List<Map<String, Object>>> getStockValorise() {
        try {
            List<Stock> stocks = stockRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Stock stock : stocks) {
                if (stock.getQuantiteActuelle().compareTo(BigDecimal.ZERO) <= 0) {
                    continue; // Skip stocks with zero or negative quantity
                }

                Map<String, Object> stockData = new HashMap<>();
                stockData.put("id", stock.getId());
                stockData.put("article", stock.getArticle());
                stockData.put("depot", stock.getDepot());
                stockData.put("quantiteActuelle", stock.getQuantiteActuelle());
                
                // Get valorisation method from depot
                String methodeCode = stock.getDepot().getMethodeValorisationStock() != null 
                    ? stock.getDepot().getMethodeValorisationStock().getCode() 
                    : "CMUP";
                
                stockData.put("methodeValorisation", stock.getDepot().getMethodeValorisationStock());
                
                // Calculate unit price based on method
                BigDecimal prixUnitaire = calculatePrixUnitaire(stock, methodeCode);
                stockData.put("prixUnitaire", prixUnitaire);
                
                // Calculate total value
                BigDecimal valeurTotale = stock.getQuantiteActuelle().multiply(prixUnitaire);
                stockData.put("valeurTotale", valeurTotale);

                result.add(stockData);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private BigDecimal calculatePrixUnitaire(Stock stock, String methode) {
        List<LotStock> lots = lotStockRepository.findAll().stream()
            .filter(lot -> lot.getArticle().getId().equals(stock.getArticle().getId())
                && lot.getDepot().getId().equals(stock.getDepot().getId())
                && lot.getQuantiteRestante().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList());

        if (lots.isEmpty()) {
            return stock.getArticle().getPrixAchatRef() != null 
                ? stock.getArticle().getPrixAchatRef() 
                : BigDecimal.ZERO;
        }

        switch (methode) {
            case "FIFO":
                // FIFO: Use oldest lot
                return lots.stream()
                    .min(Comparator.comparing(LotStock::getDateEntree))
                    .map(LotStock::getPrixUnitaireAchat)
                    .orElse(BigDecimal.ZERO);

            case "LIFO":
                // LIFO: Use newest lot
                return lots.stream()
                    .max(Comparator.comparing(LotStock::getDateEntree))
                    .map(LotStock::getPrixUnitaireAchat)
                    .orElse(BigDecimal.ZERO);

            case "CMUP":
            default:
                // CMUP: Weighted average
                BigDecimal totalValue = BigDecimal.ZERO;
                BigDecimal totalQuantity = BigDecimal.ZERO;
                
                for (LotStock lot : lots) {
                    totalValue = totalValue.add(
                        lot.getQuantiteRestante().multiply(lot.getPrixUnitaireAchat())
                    );
                    totalQuantity = totalQuantity.add(lot.getQuantiteRestante());
                }
                
                if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
                    return totalValue.divide(totalQuantity, 2, RoundingMode.HALF_UP);
                }
                return BigDecimal.ZERO;
        }
    }
}