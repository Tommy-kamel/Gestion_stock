package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mouvements")
@CrossOrigin(origins = "*")
public class MouvementStockController {

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private LotStockRepository lotStockRepository;

    @Autowired
    private SortieLotDetailRepository sortieLotDetailRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DepotRepository depotRepository;

    @GetMapping
    public ResponseEntity<List<MouvementStock>> getAllMouvements() {
        try {
            List<MouvementStock> mouvements = mouvementStockRepository.findAll();
            return ResponseEntity.ok(mouvements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createMouvement(@RequestBody MouvementRequest request) {
        try {
            Optional<Depot> depotOpt = depotRepository.findById(request.getDepotId());
            if (!depotOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Dépôt introuvable"));
            }

            Depot depot = depotOpt.get();
            List<MouvementStock> createdMouvements = new ArrayList<>();

            for (MouvementDetailRequest detail : request.getDetails()) {
                Optional<Article> articleOpt = articleRepository.findById(detail.getArticleId());
                if (!articleOpt.isPresent()) {
                    continue;
                }

                Article article = articleOpt.get();

                if ("ENTREE".equals(request.getTypeMouvement())) {
                    MouvementStock mouvement = createEntreeMouvement(article, depot, detail, request);
                    createdMouvements.add(mouvement);
                } else if ("SORTIE".equals(request.getTypeMouvement())) {
                    MouvementStock mouvement = createSortieMouvement(article, depot, detail, request);
                    createdMouvements.add(mouvement);
                }
            }

            return ResponseEntity.ok(createdMouvements);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    private MouvementStock createEntreeMouvement(Article article, Depot depot, MouvementDetailRequest detail, MouvementRequest request) {
        // Get or create stock
        Optional<Stock> stockOpt = stockRepository.findByArticleAndDepot(article, depot);
        Stock stock;
        BigDecimal quantiteAvant;

        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
            quantiteAvant = stock.getQuantiteActuelle();
        } else {
            stock = new Stock();
            stock.setArticle(article);
            stock.setDepot(depot);
            stock.setQuantiteActuelle(BigDecimal.ZERO);
            quantiteAvant = BigDecimal.ZERO;
        }

        // Create mouvement
        MouvementStock mouvement = new MouvementStock();
        mouvement.setTypeMouvement("ENTREE");
        mouvement.setQuantiteStockAvant(quantiteAvant);
        mouvement.setQuantiteEntree(detail.getQuantite());
        mouvement.setQuantiteSortie(BigDecimal.ZERO);
        mouvement.setQuantiteStockApres(quantiteAvant.add(detail.getQuantite()));
        mouvement.setPrixUnitaireMouvement(detail.getPrixUnitaire());
        mouvement.setArticle(article);
        mouvement.setDepot(depot);
        mouvement.setDateMouvement(request.getDateMouvement() != null ? request.getDateMouvement() : LocalDateTime.now());

        MouvementStock savedMouvement = mouvementStockRepository.save(mouvement);

        // Create lot
        LotStock lot = new LotStock();
        lot.setNumeroLot(generateNumeroLot(article, depot));
        lot.setArticle(article);
        lot.setDepot(depot);
        lot.setMouvementEntree(savedMouvement);
        lot.setQuantiteInitiale(detail.getQuantite());
        lot.setQuantiteRestante(detail.getQuantite());
        lot.setPrixUnitaireAchat(detail.getPrixUnitaire());
        lot.setDateEntree(savedMouvement.getDateMouvement());
        lotStockRepository.save(lot);

        // Update stock
        stock.setQuantiteActuelle(stock.getQuantiteActuelle().add(detail.getQuantite()));
        stockRepository.save(stock);

        return savedMouvement;
    }

    private MouvementStock createSortieMouvement(Article article, Depot depot, MouvementDetailRequest detail, MouvementRequest request) {
        // Get stock
        Optional<Stock> stockOpt = stockRepository.findByArticleAndDepot(article, depot);
        if (!stockOpt.isPresent() || stockOpt.get().getQuantiteActuelle().compareTo(detail.getQuantite()) < 0) {
            throw new RuntimeException("Stock insuffisant pour l'article " + article.getReference());
        }

        Stock stock = stockOpt.get();
        BigDecimal quantiteAvant = stock.getQuantiteActuelle();

        // Get valuation method
        String methodeValorisation = depot.getMethodeValorisationStock() != null 
                ? depot.getMethodeValorisationStock().getCode() 
                : "FIFO";

        // Get lots
        List<LotStock> lots = lotStockRepository.findByArticleAndDepotAndQuantiteRestanteGreaterThan(
                article, depot, BigDecimal.ZERO);

        // Sort lots according to valuation method
        if ("FIFO".equals(methodeValorisation)) {
            lots.sort(Comparator.comparing(LotStock::getDateEntree));
        } else if ("LIFO".equals(methodeValorisation)) {
            lots.sort(Comparator.comparing(LotStock::getDateEntree).reversed());
        }

        // Calculate weighted average price based on actual quantities that will be deducted
        BigDecimal prixUnitaire;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal quantiteCalculee = detail.getQuantite();
        
        for (LotStock lot : lots) {
            if (quantiteCalculee.compareTo(BigDecimal.ZERO) <= 0) break;
            
            BigDecimal quantiteAUtiliser = quantiteCalculee.min(lot.getQuantiteRestante());
            totalValue = totalValue.add(quantiteAUtiliser.multiply(lot.getPrixUnitaireAchat()));
            quantiteCalculee = quantiteCalculee.subtract(quantiteAUtiliser);
        }
        
        prixUnitaire = detail.getQuantite().compareTo(BigDecimal.ZERO) > 0 
                ? totalValue.divide(detail.getQuantite(), 2, BigDecimal.ROUND_HALF_UP) 
                : BigDecimal.ZERO;

        // Create mouvement
        MouvementStock mouvement = new MouvementStock();
        mouvement.setTypeMouvement("SORTIE");
        mouvement.setQuantiteStockAvant(quantiteAvant);
        mouvement.setQuantiteEntree(BigDecimal.ZERO);
        mouvement.setQuantiteSortie(detail.getQuantite());
        mouvement.setQuantiteStockApres(quantiteAvant.subtract(detail.getQuantite()));
        mouvement.setPrixUnitaireMouvement(prixUnitaire);
        mouvement.setArticle(article);
        mouvement.setDepot(depot);
        mouvement.setDateMouvement(request.getDateMouvement() != null ? request.getDateMouvement() : LocalDateTime.now());

        MouvementStock savedMouvement = mouvementStockRepository.save(mouvement);

        // Deduct from lots
        BigDecimal quantiteRestante = detail.getQuantite();
        for (LotStock lot : lots) {
            if (quantiteRestante.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal quantiteADeduire = quantiteRestante.min(lot.getQuantiteRestante());

            // Create sortie lot detail
            SortieLotDetail sortieLotDetail = new SortieLotDetail();
            sortieLotDetail.setLotStock(lot);
            sortieLotDetail.setMouvementSortie(savedMouvement);
            sortieLotDetail.setQuantitePrelevee(quantiteADeduire);
            sortieLotDetail.setPrixUnitaireLot(lot.getPrixUnitaireAchat());
            sortieLotDetailRepository.save(sortieLotDetail);

            // Update lot
            lot.setQuantiteRestante(lot.getQuantiteRestante().subtract(quantiteADeduire));
            lotStockRepository.save(lot);

            quantiteRestante = quantiteRestante.subtract(quantiteADeduire);
        }

        // Update stock
        stock.setQuantiteActuelle(stock.getQuantiteActuelle().subtract(detail.getQuantite()));
        stockRepository.save(stock);

        return savedMouvement;
    }

    private String generateNumeroLot(Article article, Depot depot) {
        String prefix = "LOT-" + article.getReference() + "-" + depot.getId() + "-";
        String datePart = LocalDate.now().toString().replace("-", "");
        long count = lotStockRepository.count() + 1;
        return prefix + datePart + "-" + String.format("%04d", count);
    }

    // Inner classes for request DTOs
    public static class MouvementRequest {
        private String typeMouvement;
        private Long depotId;
        private LocalDateTime dateMouvement;
        private String motif;
        private List<MouvementDetailRequest> details;

        // Getters and setters
        public String getTypeMouvement() { return typeMouvement; }
        public void setTypeMouvement(String typeMouvement) { this.typeMouvement = typeMouvement; }
        public Long getDepotId() { return depotId; }
        public void setDepotId(Long depotId) { this.depotId = depotId; }
        public LocalDateTime getDateMouvement() { return dateMouvement; }
        public void setDateMouvement(LocalDateTime dateMouvement) { this.dateMouvement = dateMouvement; }
        public String getMotif() { return motif; }
        public void setMotif(String motif) { this.motif = motif; }
        public List<MouvementDetailRequest> getDetails() { return details; }
        public void setDetails(List<MouvementDetailRequest> details) { this.details = details; }
    }

    public static class MouvementDetailRequest {
        private Long articleId;
        private BigDecimal quantite;
        private BigDecimal prixUnitaire;

        // Getters and setters
        public Long getArticleId() { return articleId; }
        public void setArticleId(Long articleId) { this.articleId = articleId; }
        public BigDecimal getQuantite() { return quantite; }
        public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }
}
