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
    private final MouvementStockRepository mouvementStockRepository;

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
            // Utiliser la table mouvement_stock pour afficher tous les mouvements (entrées et sorties)
            List<MouvementStock> mouvements = mouvementStockRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getDateMouvement().compareTo(m1.getDateMouvement()))
                .collect(Collectors.toList());
            
            List<Map<String, Object>> result = new ArrayList<>();

            for (MouvementStock mvt : mouvements) {
                Map<String, Object> mvtData = new HashMap<>();
                mvtData.put("id", mvt.getId());
                mvtData.put("typeMouvement", mvt.getTypeMouvement());
                mvtData.put("article", mvt.getArticle());
                mvtData.put("depot", mvt.getDepot());
                mvtData.put("dateMouvement", mvt.getDateMouvement());
                
                // Quantités selon le type
                if ("ENTREE".equals(mvt.getTypeMouvement())) {
                    mvtData.put("quantite", mvt.getQuantiteEntree());
                    mvtData.put("prixUnitaire", mvt.getPrixUnitaireMouvement());
                    mvtData.put("valeurTotale", mvt.getQuantiteEntree().multiply(mvt.getPrixUnitaireMouvement()));
                } else {
                    mvtData.put("quantite", mvt.getQuantiteSortie());
                    mvtData.put("prixUnitaire", mvt.getPrixUnitaireMouvement());
                    mvtData.put("valeurTotale", mvt.getQuantiteSortie().multiply(mvt.getPrixUnitaireMouvement()));
                }
                
                mvtData.put("quantiteStockAvant", mvt.getQuantiteStockAvant());
                mvtData.put("quantiteStockApres", mvt.getQuantiteStockApres());
                
                // Get valorisation method from depot
                mvtData.put("methodeValorisation", mvt.getDepot().getMethodeValorisationStock());

                result.add(mvtData);
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

        // Pour la valorisation du stock (état), on calcule toujours le coût moyen pondéré
        // car le stock contient des lots différents avec des prix différents
        // La méthode FIFO/LIFO/CMUP s'applique lors des SORTIES, pas pour l'évaluation du stock
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