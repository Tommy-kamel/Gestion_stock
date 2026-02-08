package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventes")
@CrossOrigin(origins = "*")
public class VenteController {

    @Autowired
    private DemandeAchatClientRepository demandeAchatClientRepository;

    @Autowired
    private DemandeAchatClientDetailsRepository demandeAchatClientDetailsRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ProformaVenteRepository proformaVenteRepository;

    @Autowired
    private ProformaVenteDetailsRepository proformaVenteDetailsRepository;

    @Autowired
    private BonCommandeVenteRepository bonCommandeVenteRepository;

    @Autowired
    private BonCommandeVenteDetailsRepository bonCommandeVenteDetailsRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DepotRepository depotRepository;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private LotStockRepository lotStockRepository;

    @Autowired
    private SortieLotDetailRepository sortieLotDetailRepository;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private CaisseMouvementRepository caisseMouvementRepository;

    @Autowired
    private PaiementVenteRepository paiementVenteRepository;

    @Autowired
    private PaiementVenteDetailsRepository paiementVenteDetailsRepository;

    // DTOs
    public static class CreateDemandeAchatClientRequest {
        private LocalDate dateDemande;
        private Long clientId;
        private LocalDate dateSouhaitee;
        private List<CreateDemandeAchatClientDetailRequest> details;

        // Getters and setters
        public LocalDate getDateDemande() { return dateDemande; }
        public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }
        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }
        public LocalDate getDateSouhaitee() { return dateSouhaitee; }
        public void setDateSouhaitee(LocalDate dateSouhaitee) { this.dateSouhaitee = dateSouhaitee; }
        public List<CreateDemandeAchatClientDetailRequest> getDetails() { return details; }
        public void setDetails(List<CreateDemandeAchatClientDetailRequest> details) { this.details = details; }
    }

    public static class CreateDemandeAchatClientDetailRequest {
        private Long articleId;
        private BigDecimal quantiteDemandee;
        private BigDecimal prixUnitaire;

        // Getters and setters
        public Long getArticleId() { return articleId; }
        public void setArticleId(Long articleId) { this.articleId = articleId; }
        public BigDecimal getQuantiteDemandee() { return quantiteDemandee; }
        public void setQuantiteDemandee(BigDecimal quantiteDemandee) { this.quantiteDemandee = quantiteDemandee; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }

    // GET - Récupérer toutes les demandes d'achat client
    @GetMapping("/demandes-client")
    public ResponseEntity<List<DemandeAchatClient>> getAllDemandesAchatClient() {
        try {
            List<DemandeAchatClient> demandes = demandeAchatClientRepository.findAll();
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Récupérer une demande d'achat client par ID
    @GetMapping("/demandes-client/{id}")
    public ResponseEntity<DemandeAchatClient> getDemandeAchatClient(@PathVariable Long id) {
        Optional<DemandeAchatClient> demande = demandeAchatClientRepository.findById(id);
        return demande.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Créer une nouvelle demande d'achat client
    @PostMapping("/demandes-client")
    public ResponseEntity<?> createDemandeAchatClient(@RequestBody CreateDemandeAchatClientRequest request) {
        try {
            // Validation
            if (request.getClientId() == null || request.getDetails() == null || request.getDetails().isEmpty()) {
                return ResponseEntity.badRequest().body("Client et détails requis");
            }

            // Récupérer le client
            Optional<Client> clientOpt = clientRepository.findById(request.getClientId());
            if (!clientOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Client introuvable");
            }

            // Récupérer le statut BROUILLON
            Optional<Status> statusOpt = statusRepository.findByCode("BROUILLON");
            if (!statusOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Statut BROUILLON introuvable");
            }

            // Générer le numéro DA
            String numeroDa = generateNumeroDaClient();

            // Créer la demande d'achat client
            DemandeAchatClient demande = new DemandeAchatClient();
            demande.setNumeroDa(numeroDa);
            demande.setDateDemande(request.getDateDemande() != null ? request.getDateDemande() : LocalDate.now());
            demande.setClient(clientOpt.get());
            demande.setDateSouhaitee(request.getDateSouhaitee());
            demande.setStatus(statusOpt.get());
            demande.setDateCreation(LocalDateTime.now());

            // Sauvegarder la demande
            DemandeAchatClient savedDemande = demandeAchatClientRepository.save(demande);

            // Créer les détails
            for (CreateDemandeAchatClientDetailRequest detailReq : request.getDetails()) {
                Optional<Article> articleOpt = articleRepository.findById(detailReq.getArticleId());
                if (!articleOpt.isPresent()) {
                    continue;
                }

                DemandeAchatClientDetails detail = new DemandeAchatClientDetails();
                detail.setDemandeAchatClient(savedDemande);
                detail.setArticle(articleOpt.get());
                detail.setQuantiteDemandee(detailReq.getQuantiteDemandee());
                detail.setPrixUnitaire(detailReq.getPrixUnitaire() != null ? detailReq.getPrixUnitaire() : BigDecimal.ZERO);

                demandeAchatClientDetailsRepository.save(detail);
            }

            // Recharger la demande avec les détails
            DemandeAchatClient result = demandeAchatClientRepository.findById(savedDemande.getId()).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    // PUT - Valider une demande d'achat client
    @PutMapping("/demandes-client/{id}/valider")
    public ResponseEntity<?> validerDemandeAchatClient(@PathVariable Long id) {
        try {
            Optional<DemandeAchatClient> demandeOpt = demandeAchatClientRepository.findById(id);
            if (!demandeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            DemandeAchatClient demande = demandeOpt.get();
            Optional<Status> statusValideOpt = statusRepository.findByCode("VALIDE");
            if (!statusValideOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Statut VALIDE introuvable");
            }

            demande.setStatus(statusValideOpt.get());
            DemandeAchatClient updated = demandeAchatClientRepository.save(demande);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la validation: " + e.getMessage());
        }
    }

    // PUT - Soumettre une demande d'achat client
    @PutMapping("/demandes-client/{id}/soumettre")
    public ResponseEntity<?> soumettreDemandeAchatClient(@PathVariable Long id) {
        try {
            Optional<DemandeAchatClient> demandeOpt = demandeAchatClientRepository.findById(id);
            if (!demandeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            DemandeAchatClient demande = demandeOpt.get();
            Optional<Status> statusSoumisOpt = statusRepository.findByCode("SOUMIS");
            if (!statusSoumisOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Statut SOUMIS introuvable");
            }

            demande.setStatus(statusSoumisOpt.get());
            DemandeAchatClient updated = demandeAchatClientRepository.save(demande);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la soumission: " + e.getMessage());
        }
    }

    // Générer un numéro de DA client unique
    private String generateNumeroDaClient() {
        String prefix = "DAC-";
        long count = demandeAchatClientRepository.count();
        String numero;
        do {
            count++;
            numero = prefix + String.format("%05d", count);
        } while (demandeAchatClientRepository.findByNumeroDa(numero) != null);
        return numero;
    }

    // ================ PROFORMAS VENTE ================

    // DTOs pour Proforma
    public static class CreateProformaVenteRequest {
        private Long demandeAchatClientId;
        private LocalDate dateProforma;
        private List<CreateProformaVenteDetailRequest> details;

        public Long getDemandeAchatClientId() { return demandeAchatClientId; }
        public void setDemandeAchatClientId(Long demandeAchatClientId) { this.demandeAchatClientId = demandeAchatClientId; }
        public LocalDate getDateProforma() { return dateProforma; }
        public void setDateProforma(LocalDate dateProforma) { this.dateProforma = dateProforma; }
        public List<CreateProformaVenteDetailRequest> getDetails() { return details; }
        public void setDetails(List<CreateProformaVenteDetailRequest> details) { this.details = details; }
    }

    public static class CreateProformaVenteDetailRequest {
        private Long articleId;
        private BigDecimal quantite;
        private BigDecimal prixUnitaire;

        public Long getArticleId() { return articleId; }
        public void setArticleId(Long articleId) { this.articleId = articleId; }
        public BigDecimal getQuantite() { return quantite; }
        public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }

    // GET - Récupérer toutes les DA client validées (pour création proforma)
    @GetMapping("/demandes-client/validees")
    public ResponseEntity<List<DemandeAchatClient>> getDemandesClientValidees() {
        try {
            Optional<Status> statusValideOpt = statusRepository.findByCode("VALIDE");
            if (!statusValideOpt.isPresent()) {
                return ResponseEntity.ok(List.of());
            }
            List<DemandeAchatClient> demandes = demandeAchatClientRepository.findByStatusId(statusValideOpt.get().getId());
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Récupérer toutes les proformas vente
    @GetMapping("/proformas")
    public ResponseEntity<List<ProformaVente>> getAllProformasVente() {
        try {
            List<ProformaVente> proformas = proformaVenteRepository.findAll();
            return ResponseEntity.ok(proformas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Créer une proforma vente basée sur une DA client
    @PostMapping("/proformas")
    public ResponseEntity<?> createProformaVente(@RequestBody CreateProformaVenteRequest request) {
        try {
            // Validation
            if (request.getDemandeAchatClientId() == null || request.getDetails() == null || request.getDetails().isEmpty()) {
                return ResponseEntity.badRequest().body("Demande client et détails requis");
            }

            // Récupérer la demande d'achat client
            Optional<DemandeAchatClient> demandeOpt = demandeAchatClientRepository.findById(request.getDemandeAchatClientId());
            if (!demandeOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Demande d'achat client introuvable");
            }

            DemandeAchatClient demande = demandeOpt.get();

            // Vérifier si un proforma existe déjà pour cette demande
            List<ProformaVente> proformasExistants = proformaVenteRepository.findByDemandeAchatClientId(request.getDemandeAchatClientId());
            if (!proformasExistants.isEmpty()) {
                return ResponseEntity.badRequest().body("Un proforma existe déjà pour cette demande d'achat client");
            }

            // Vérifier le stock disponible pour chaque article
            StringBuilder stockInsuffisant = new StringBuilder();
            for (CreateProformaVenteDetailRequest detailReq : request.getDetails()) {
                Optional<Article> articleOpt = articleRepository.findById(detailReq.getArticleId());
                if (!articleOpt.isPresent()) {
                    continue;
                }
                Article article = articleOpt.get();
                
                // Calculer le stock total disponible pour cet article (tous dépôts)
                List<Stock> stocks = stockRepository.findAll();
                BigDecimal stockTotal = stocks.stream()
                    .filter(s -> s.getArticle().getId().equals(detailReq.getArticleId()))
                    .map(Stock::getQuantiteActuelle)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                if (stockTotal.compareTo(detailReq.getQuantite()) < 0) {
                    stockInsuffisant.append("Article '")
                        .append(article.getDesignation())
                        .append("': stock disponible ")
                        .append(stockTotal)
                        .append(", demandé ")
                        .append(detailReq.getQuantite())
                        .append(". ");
                }
            }
            
            if (stockInsuffisant.length() > 0) {
                return ResponseEntity.badRequest().body("Stock insuffisant: " + stockInsuffisant.toString());
            }

            // Récupérer le status VALIDE
            Optional<Status> statusOpt = statusRepository.findByCode("VALIDE");
            if (!statusOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status VALIDE introuvable");
            }

            // Générer le numéro de proforma
            String numeroProforma = generateNumeroProformaVente();

            // Créer la proforma vente
            ProformaVente proforma = new ProformaVente();
            proforma.setNumeroProforma(numeroProforma);
            proforma.setDateProforma(request.getDateProforma() != null ? request.getDateProforma() : LocalDate.now());
            proforma.setDemandeAchatClient(demande);
            proforma.setStatus(statusOpt.get());

            // Calculer le montant total
            BigDecimal montantTotal = BigDecimal.ZERO;
            for (CreateProformaVenteDetailRequest detailReq : request.getDetails()) {
                BigDecimal montantLigne = detailReq.getQuantite().multiply(detailReq.getPrixUnitaire());
                montantTotal = montantTotal.add(montantLigne);
            }
            proforma.setMontantTtc(montantTotal);

            // Sauvegarder la proforma
            ProformaVente savedProforma = proformaVenteRepository.save(proforma);

            // Créer les détails
            for (CreateProformaVenteDetailRequest detailReq : request.getDetails()) {
                Optional<Article> articleOpt = articleRepository.findById(detailReq.getArticleId());
                if (!articleOpt.isPresent()) {
                    continue;
                }

                ProformaVenteDetails detail = new ProformaVenteDetails();
                detail.setProformaVente(savedProforma);
                detail.setArticle(articleOpt.get());
                detail.setQuantite(detailReq.getQuantite());
                detail.setPrixUnitaire(detailReq.getPrixUnitaire());

                proformaVenteDetailsRepository.save(detail);
            }

            // Recharger la proforma avec les détails
            ProformaVente result = proformaVenteRepository.findById(savedProforma.getId()).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    // Générer un numéro de proforma vente unique
    private String generateNumeroProformaVente() {
        String prefix = "PV-";
        long count = proformaVenteRepository.count();
        String numero;
        do {
            count++;
            numero = prefix + String.format("%05d", count);
        } while (proformaVenteRepository.findByNumeroProforma(numero) != null);
        return numero;
    }

    // ========== BONS DE COMMANDE VENTE ==========

    // GET - Récupérer les proformas validés
    @GetMapping("/proformas/valides")
    public ResponseEntity<List<ProformaVente>> getProformasValides() {
        try {
            Optional<Status> statusOpt = statusRepository.findByCode("VALIDE");
            if (!statusOpt.isPresent()) {
                return ResponseEntity.ok(List.of());
            }
            
            List<ProformaVente> proformas = proformaVenteRepository.findAll();
            List<ProformaVente> proformasValides = proformas.stream()
                .filter(p -> p.getStatus() != null && p.getStatus().getCode().equals("VALIDE"))
                .toList();
            
            return ResponseEntity.ok(proformasValides);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Récupérer tous les bons de commande vente
    @GetMapping("/bons-commande")
    public ResponseEntity<List<BonCommandeVente>> getBonsCommande() {
        try {
            return ResponseEntity.ok(bonCommandeVenteRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO pour créer un bon de commande
    public static class CreateBonCommandeVenteRequest {
        private Long proformaVenteId;
        private LocalDate dateCommande;

        public Long getProformaVenteId() { return proformaVenteId; }
        public void setProformaVenteId(Long proformaVenteId) { this.proformaVenteId = proformaVenteId; }
        public LocalDate getDateCommande() { return dateCommande; }
        public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    }

    // POST - Créer un bon de commande à partir d'un proforma
    @PostMapping("/bons-commande")
    public ResponseEntity<?> createBonCommandeVente(@RequestBody CreateBonCommandeVenteRequest request) {
        try {
            // Récupérer le proforma
            Optional<ProformaVente> proformaOpt = proformaVenteRepository.findById(request.getProformaVenteId());
            if (!proformaOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Proforma introuvable");
            }
            
            ProformaVente proforma = proformaOpt.get();
            
            // Vérifier qu'un BC n'existe pas déjà
            List<BonCommandeVente> bcsExistants = bonCommandeVenteRepository.findByProformaVenteId(request.getProformaVenteId());
            if (!bcsExistants.isEmpty()) {
                return ResponseEntity.badRequest().body("Un bon de commande existe déjà pour ce proforma");
            }
            
            // Récupérer le status EN_COURS
            Optional<Status> statusOpt = statusRepository.findByCode("EN_COURS");
            if (!statusOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status EN_COURS introuvable");
            }
            
            // Générer le numéro de BC
            String numeroBc = generateNumeroBonCommandeVente();
            
            // Créer le bon de commande
            BonCommandeVente bc = new BonCommandeVente();
            bc.setNumeroBc(numeroBc);
            bc.setDateCommande(request.getDateCommande() != null ? request.getDateCommande() : LocalDate.now());
            bc.setProformaVente(proforma);
            bc.setClient(proforma.getDemandeAchatClient().getClient());
            bc.setStatus(statusOpt.get());
            bc.setMontantTtc(proforma.getMontantTtc());
            
            BonCommandeVente savedBc = bonCommandeVenteRepository.save(bc);
            
            // Créer les détails depuis le proforma
            for (ProformaVenteDetails proformaDetail : proforma.getDetails()) {
                BonCommandeVenteDetails bcDetail = new BonCommandeVenteDetails();
                bcDetail.setBonCommandeVente(savedBc);
                bcDetail.setArticle(proformaDetail.getArticle());
                bcDetail.setQuantite(proformaDetail.getQuantite());
                bcDetail.setPrixUnitaire(proformaDetail.getPrixUnitaire());
                
                bonCommandeVenteDetailsRepository.save(bcDetail);
            }
            
            // Recharger avec les détails
            BonCommandeVente result = bonCommandeVenteRepository.findById(savedBc.getId()).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    private String generateNumeroBonCommandeVente() {
        String prefix = "BCV-";
        long count = bonCommandeVenteRepository.count();
        String numero;
        do {
            count++;
            numero = prefix + String.format("%05d", count);
        } while (bonCommandeVenteRepository.findByNumeroBc(numero) != null);
        return numero;
    }

    // DTO pour livrer et encaisser
    public static class LivrerEncaisserRequest {
        private Long depotId;
        private Long caisseId;

        public Long getDepotId() { return depotId; }
        public void setDepotId(Long depotId) { this.depotId = depotId; }
        public Long getCaisseId() { return caisseId; }
        public void setCaisseId(Long caisseId) { this.caisseId = caisseId; }
    }

    // POST - Livrer et encaisser un bon de commande
    @PostMapping("/bons-commande/{id}/livrer-encaisser")
    public ResponseEntity<?> livrerEtEncaisser(@PathVariable Long id, @RequestBody LivrerEncaisserRequest request) {
        try {
            // Récupérer le BC
            Optional<BonCommandeVente> bcOpt = bonCommandeVenteRepository.findById(id);
            if (!bcOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Bon de commande introuvable");
            }
            
            BonCommandeVente bc = bcOpt.get();
            
            // Récupérer le dépôt
            Optional<Depot> depotOpt = depotRepository.findById(request.getDepotId());
            if (!depotOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Dépôt introuvable");
            }
            Depot depot = depotOpt.get();
            
            // Récupérer la caisse
            Optional<Caisse> caisseOpt = caisseRepository.findById(request.getCaisseId());
            if (!caisseOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Caisse introuvable");
            }
            Caisse caisse = caisseOpt.get();
            
            // Récupérer les détails du BC
            List<BonCommandeVenteDetails> details = bonCommandeVenteDetailsRepository.findByBonCommandeVenteId(id);
            if (details.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucun détail trouvé pour ce bon de commande");
            }
            
            // Traiter chaque article
            for (BonCommandeVenteDetails detail : details) {
                Article article = detail.getArticle();
                BigDecimal quantiteASortir = detail.getQuantite();
                
                // Vérifier et mettre à jour le stock
                Optional<Stock> stockOpt = stockRepository.findByArticleAndDepot(article, depot);
                if (!stockOpt.isPresent() || stockOpt.get().getQuantiteActuelle().compareTo(quantiteASortir) < 0) {
                    return ResponseEntity.badRequest().body("Stock insuffisant pour l'article: " + article.getDesignation());
                }
                
                Stock stock = stockOpt.get();
                BigDecimal stockAvant = stock.getQuantiteActuelle();
                BigDecimal stockApres = stockAvant.subtract(quantiteASortir);
                
                // Créer le mouvement de stock (SORTIE)
                MouvementStock mouvement = new MouvementStock();
                mouvement.setDateMouvement(LocalDateTime.now());
                mouvement.setTypeMouvement("SORTIE");
                mouvement.setQuantiteStockAvant(stockAvant);
                mouvement.setQuantiteEntree(BigDecimal.ZERO);
                mouvement.setQuantiteSortie(quantiteASortir);
                mouvement.setQuantiteStockApres(stockApres);
                mouvement.setPrixUnitaireMouvement(detail.getPrixUnitaire());
                mouvement.setArticle(article);
                mouvement.setDepot(depot);
                
                MouvementStock savedMouvement = mouvementStockRepository.save(mouvement);
                
                // Mettre à jour le stock
                stock.setQuantiteActuelle(stockApres);
                stock.setDateMaj(LocalDateTime.now());
                stockRepository.save(stock);
                
                // Gérer les lots FIFO
                List<LotStock> lotsDisponibles = lotStockRepository.findLotsDisponiblesForFIFO(article.getId(), depot.getId());
                BigDecimal quantiteRestanteASortir = quantiteASortir;
                
                for (LotStock lot : lotsDisponibles) {
                    if (quantiteRestanteASortir.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                    
                    BigDecimal quantiteAPreleversurLot = quantiteRestanteASortir.min(lot.getQuantiteRestante());
                    
                    // Créer sortie_lot_detail
                    SortieLotDetail sortieLot = new SortieLotDetail();
                    sortieLot.setMouvementSortie(savedMouvement);
                    sortieLot.setLotStock(lot);
                    sortieLot.setQuantitePrelevee(quantiteAPreleversurLot);
                    sortieLot.setPrixUnitaireLot(lot.getPrixUnitaireAchat());
                    
                    sortieLotDetailRepository.save(sortieLot);
                    
                    // Mettre à jour le lot
                    lot.setQuantiteRestante(lot.getQuantiteRestante().subtract(quantiteAPreleversurLot));
                    lotStockRepository.save(lot);
                    
                    quantiteRestanteASortir = quantiteRestanteASortir.subtract(quantiteAPreleversurLot);
                }
            }
            
            // Gérer l'encaissement
            BigDecimal soldeAvant = caisse.getSoldeActuel();
            BigDecimal montantEncaisse = bc.getMontantTtc();
            BigDecimal soldeApres = soldeAvant.add(montantEncaisse);
            
            // Créer le mouvement de caisse
            CaisseMouvement caisseMvt = new CaisseMouvement();
            caisseMvt.setDateMouvement(LocalDateTime.now());
            caisseMvt.setLibelleOperation("Encaissement BC " + bc.getNumeroBc());
            caisseMvt.setMontantEntree(montantEncaisse);
            caisseMvt.setMontantSortie(BigDecimal.ZERO);
            caisseMvt.setSoldeAvant(soldeAvant);
            caisseMvt.setSoldeApres(soldeApres);
            caisseMvt.setCaisse(caisse);
            
            CaisseMouvement savedCaisseMvt = caisseMouvementRepository.save(caisseMvt);
            
            // Mettre à jour le solde de la caisse
            caisse.setSoldeActuel(soldeApres);
            caisseRepository.save(caisse);
            
            // Créer une facture vente si nécessaire (simplifié)
            // Note: Dans une implémentation complète, vous créeriez d'abord une facture_vente
            // Pour simplifier, nous créons directement le paiement lié au BC
            
            // Mettre à jour le status du BC
            Optional<Status> statusLivreOpt = statusRepository.findByCode("LIVRE");
            if (statusLivreOpt.isPresent()) {
                bc.setStatus(statusLivreOpt.get());
                bonCommandeVenteRepository.save(bc);
            }
            
            return ResponseEntity.ok().body("Livraison et encaissement effectués avec succès");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la livraison et l'encaissement: " + e.getMessage());
        }
    }
}
