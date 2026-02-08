package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bons-commande-achat")
@CrossOrigin(origins = "*")
public class BonCommandeAchatController {

    @Autowired
    private BonCommandeAchatRepository bonCommandeAchatRepository;

    @Autowired
    private BonCommandeAchatDetailsRepository bonCommandeAchatDetailsRepository;

    @Autowired
    private ProformaFournisseurRepository proformaFournisseurRepository;

    @Autowired
    private FactureAchatRepository factureAchatRepository;

    @Autowired
    private FactureAchatDetailsRepository factureAchatDetailsRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private LotStockRepository lotStockRepository;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private CaisseMouvementRepository caisseMouvementRepository;

    @Autowired
    private PaiementAchatRepository paiementAchatRepository;

    @Autowired
    private PaiementAchatDetailsRepository paiementAchatDetailsRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private DepotRepository depotRepository;

    // DTOs
    public static class CreateBonCommandeRequest {
        private Long proformaFournisseurId;

        public Long getProformaFournisseurId() { return proformaFournisseurId; }
        public void setProformaFournisseurId(Long proformaFournisseurId) { this.proformaFournisseurId = proformaFournisseurId; }
    }

    public static class LivrerEtPayerRequest {
        private Long caisseId;
        private String numeroFactureFournisseur;

        public Long getCaisseId() { return caisseId; }
        public void setCaisseId(Long caisseId) { this.caisseId = caisseId; }
        public String getNumeroFactureFournisseur() { return numeroFactureFournisseur; }
        public void setNumeroFactureFournisseur(String numeroFactureFournisseur) { this.numeroFactureFournisseur = numeroFactureFournisseur; }
    }

    @GetMapping
    public ResponseEntity<List<BonCommandeAchat>> getAllBonsCommande() {
        List<BonCommandeAchat> bcs = bonCommandeAchatRepository.findAll();
        return ResponseEntity.ok(bcs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBonCommandeById(@PathVariable Long id) {
        Optional<BonCommandeAchat> bcOpt = bonCommandeAchatRepository.findById(id);
        if (!bcOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        BonCommandeAchat bc = bcOpt.get();
        List<BonCommandeAchatDetails> details = bonCommandeAchatDetailsRepository.findByBonCommandeAchat(bc);
        
        // Créer un DTO avec les détails
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", bc.getId());
        response.put("numeroBc", bc.getNumeroBc());
        response.put("dateCommande", bc.getDateCommande());
        response.put("montantTtc", bc.getMontantTtc());
        response.put("status", bc.getStatus());
        response.put("proformaFournisseur", bc.getProformaFournisseur());
        response.put("fournisseur", bc.getProformaFournisseur() != null ? bc.getProformaFournisseur().getFournisseur() : null);
        response.put("details", details);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BonCommandeAchat> createBonCommande(@RequestBody CreateBonCommandeRequest request) {
        try {
            Optional<ProformaFournisseur> proformaOpt = proformaFournisseurRepository.findById(request.getProformaFournisseurId());
            if (!proformaOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            ProformaFournisseur proforma = proformaOpt.get();
            
            // Créer le BC
            BonCommandeAchat bc = new BonCommandeAchat();
            bc.setNumeroBc("BC-" + LocalDate.now().getYear() + "-" + String.format("%04d", bonCommandeAchatRepository.count() + 1));
            bc.setDateCommande(LocalDate.now());
            bc.setProformaFournisseur(proforma);
            bc.setMontantTtc(proforma.getMontantTtc());

            // Status = BROUILLON
            Optional<Status> statusOpt = statusRepository.findByCode("BROUILLON");
            bc.setStatus(statusOpt.orElse(null));

            BonCommandeAchat savedBc = bonCommandeAchatRepository.save(bc);

            // Copier les détails du proforma
            for (ProformaFournisseurDetails proformaDetail : proforma.getDetails()) {
                BonCommandeAchatDetails detail = new BonCommandeAchatDetails();
                detail.setBonCommandeAchat(savedBc);
                detail.setArticle(proformaDetail.getArticle());
                detail.setQuantite(proformaDetail.getQuantite());
                detail.setPrixUnitaire(proformaDetail.getPrixUnitaire());
                bonCommandeAchatDetailsRepository.save(detail);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedBc);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/livrer-et-payer")
    @Transactional
    public ResponseEntity<String> livrerEtPayer(@PathVariable Long id, @RequestBody LivrerEtPayerRequest request) {
        try {
            // 1. Récupérer le BC
            Optional<BonCommandeAchat> bcOpt = bonCommandeAchatRepository.findById(id);
            if (!bcOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Bon de commande introuvable");
            }
            BonCommandeAchat bc = bcOpt.get();

            // 2. Récupérer le dépôt de la demande d'achat
            Depot depot = bc.getProformaFournisseur().getDemandeAchat().getDepot();
            if (depot == null) {
                return ResponseEntity.badRequest().body("Dépôt introuvable");
            }

            // 3. Créer la facture achat
            FactureAchat facture = new FactureAchat();
            facture.setNumeroFactureFournisseur(request.getNumeroFactureFournisseur());
            facture.setDateFacture(LocalDate.now());
            facture.setBonCommandeAchat(bc);
            facture.setMontantTtc(bc.getMontantTtc());
            
            Optional<Status> statusValide = statusRepository.findByCode("VALIDE");
            facture.setStatus(statusValide.orElse(null));
            
            FactureAchat savedFacture = factureAchatRepository.save(facture);

            // 4. Créer les détails de facture et mettre à jour le stock
            List<BonCommandeAchatDetails> bcDetails = bonCommandeAchatDetailsRepository.findByBonCommandeAchat(bc);
            
            for (BonCommandeAchatDetails bcDetail : bcDetails) {
                // 4a. Créer le détail de facture
                FactureAchatDetails factureDetail = new FactureAchatDetails();
                factureDetail.setFactureAchat(savedFacture);
                factureDetail.setArticle(bcDetail.getArticle());
                factureDetail.setQuantite(bcDetail.getQuantite());
                factureDetail.setPrixUnitaire(bcDetail.getPrixUnitaire());
                factureAchatDetailsRepository.save(factureDetail);

                // 4b. Mettre à jour le stock
                Optional<Stock> stockOpt = stockRepository.findByArticleAndDepot(bcDetail.getArticle(), depot);
                Stock stock;
                BigDecimal quantiteAvant;
                
                if (stockOpt.isPresent()) {
                    stock = stockOpt.get();
                    quantiteAvant = stock.getQuantiteActuelle();
                    stock.setQuantiteActuelle(quantiteAvant.add(bcDetail.getQuantite()));
                } else {
                    stock = new Stock();
                    stock.setArticle(bcDetail.getArticle());
                    stock.setDepot(depot);
                    quantiteAvant = BigDecimal.ZERO;
                    stock.setQuantiteActuelle(bcDetail.getQuantite());
                }
                stock.setDateMaj(LocalDateTime.now());
                stockRepository.save(stock);

                // 4c. Créer le mouvement de stock
                MouvementStock mouvement = new MouvementStock();
                mouvement.setDateMouvement(LocalDateTime.now());
                mouvement.setTypeMouvement("ENTREE");
                mouvement.setQuantiteStockAvant(quantiteAvant);
                mouvement.setQuantiteEntree(bcDetail.getQuantite());
                mouvement.setQuantiteSortie(BigDecimal.ZERO);
                mouvement.setQuantiteStockApres(stock.getQuantiteActuelle());
                mouvement.setPrixUnitaireMouvement(bcDetail.getPrixUnitaire());
                mouvement.setArticle(bcDetail.getArticle());
                mouvement.setDepot(depot);
                MouvementStock savedMouvement = mouvementStockRepository.save(mouvement);

                // 4d. Créer le lot de stock
                LotStock lot = new LotStock();
                lot.setNumeroLot("LOT-" + LocalDate.now().getYear() + "-" + System.currentTimeMillis());
                lot.setArticle(bcDetail.getArticle());
                lot.setDepot(depot);
                lot.setDateEntree(LocalDateTime.now());
                lot.setMouvementEntree(savedMouvement);
                lot.setQuantiteInitiale(bcDetail.getQuantite());
                lot.setQuantiteRestante(bcDetail.getQuantite());
                lot.setPrixUnitaireAchat(bcDetail.getPrixUnitaire());
                lotStockRepository.save(lot);
            }

            // 5. Mettre à jour la caisse
            Optional<Caisse> caisseOpt = caisseRepository.findById(request.getCaisseId());
            if (!caisseOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Caisse introuvable");
            }
            Caisse caisse = caisseOpt.get();
            BigDecimal soldeAvant = caisse.getSoldeActuel();
            BigDecimal soldeApres = soldeAvant.subtract(bc.getMontantTtc());
            caisse.setSoldeActuel(soldeApres);
            caisseRepository.save(caisse);

            // 6. Créer le mouvement de caisse
            CaisseMouvement caisseMouvement = new CaisseMouvement();
            caisseMouvement.setDateMouvement(LocalDateTime.now());
            caisseMouvement.setLibelleOperation("Paiement BC " + bc.getNumeroBc());
            caisseMouvement.setMontantEntree(BigDecimal.ZERO);
            caisseMouvement.setMontantSortie(bc.getMontantTtc());
            caisseMouvement.setSoldeAvant(soldeAvant);
            caisseMouvement.setSoldeApres(soldeApres);
            caisseMouvement.setCaisse(caisse);
            CaisseMouvement savedCaisseMouvement = caisseMouvementRepository.save(caisseMouvement);

            // 7. Créer le paiement achat
            PaiementAchat paiement = new PaiementAchat();
            paiement.setNumeroPaiement("PA-" + LocalDate.now().getYear() + "-" + String.format("%04d", paiementAchatRepository.count() + 1));
            paiement.setDatePaiement(LocalDate.now());
            paiement.setFactureAchat(savedFacture);
            paiement.setCaisseMouvement(savedCaisseMouvement);
            paiement.setMontantTotalPaye(bc.getMontantTtc());
            PaiementAchat savedPaiement = paiementAchatRepository.save(paiement);

            // 8. Créer le détail du paiement
            PaiementAchatDetails paiementDetail = new PaiementAchatDetails();
            paiementDetail.setPaiementAchat(savedPaiement);
            paiementDetail.setMontant(bc.getMontantTtc());
            paiementDetail.setReferenceExterne("Paiement complet BC " + bc.getNumeroBc());
            paiementAchatDetailsRepository.save(paiementDetail);

            // 9. Mettre à jour le statut du BC
            bc.setStatus(statusValide.orElse(null));
            bonCommandeAchatRepository.save(bc);

            return ResponseEntity.ok("Livraison et paiement effectués avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }
    }
}
