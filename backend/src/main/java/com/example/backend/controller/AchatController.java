package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.service.DemandeAchatService;
import com.example.backend.service.DemandeAchatDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/achats")
@CrossOrigin(origins = "*")
public class AchatController {

    // DTOs
    public static class CreateDemandeAchatRequest {
        private LocalDate dateDemande;
        private Long entrepriseId;
        private Long siteId;
        private Long depotId;
        private Long demandeurId;
        private String motifAchat;
        private List<CreateDemandeAchatDetailRequest> details;

        // Getters and setters
        public LocalDate getDateDemande() { return dateDemande; }
        public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }
        public Long getEntrepriseId() { return entrepriseId; }
        public void setEntrepriseId(Long entrepriseId) { this.entrepriseId = entrepriseId; }
        public Long getSiteId() { return siteId; }
        public void setSiteId(Long siteId) { this.siteId = siteId; }
        public Long getDepotId() { return depotId; }
        public void setDepotId(Long depotId) { this.depotId = depotId; }
        public Long getDemandeurId() { return demandeurId; }
        public void setDemandeurId(Long demandeurId) { this.demandeurId = demandeurId; }
        public String getMotifAchat() { return motifAchat; }
        public void setMotifAchat(String motifAchat) { this.motifAchat = motifAchat; }
        public List<CreateDemandeAchatDetailRequest> getDetails() { return details; }
        public void setDetails(List<CreateDemandeAchatDetailRequest> details) { this.details = details; }
    }

    public static class CreateDemandeAchatDetailRequest {
        private Long articleId;
        private Integer quantiteDemandee;
        private Double prixUnitaire;

        // Getters and setters
        public Long getArticleId() { return articleId; }
        public void setArticleId(Long articleId) { this.articleId = articleId; }
        public Integer getQuantiteDemandee() { return quantiteDemandee; }
        public void setQuantiteDemandee(Integer quantiteDemandee) { this.quantiteDemandee = quantiteDemandee; }
        public Double getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }

    @Autowired
    private DemandeAchatService demandeAchatService;

    @Autowired
    private DemandeAchatDetailsService demandeAchatDetailsService;

    @Autowired
    private com.example.backend.repository.EntrepriseRepository entrepriseRepository;

    @Autowired
    private com.example.backend.repository.SiteRepository siteRepository;

    @Autowired
    private com.example.backend.repository.DepotRepository depotRepository;

    @Autowired
    private com.example.backend.repository.ArticleRepository articleRepository;

    @PostMapping("/demandes")
    public ResponseEntity<DemandeAchat> createDemandeAchat(@RequestBody CreateDemandeAchatRequest request) {
        try {
            DemandeAchat demandeAchat = new DemandeAchat();
            demandeAchat.setDateDemande(request.getDateDemande());
            demandeAchat.setMotifAchat(request.getMotifAchat());
            demandeAchat.setDateCreation(LocalDateTime.now());

            // Generate numero DA
            String numeroDa = "DA-" + LocalDate.now().getYear() + "-" + String.format("%04d", demandeAchatService.count() + 1);
            demandeAchat.setNumeroDa(numeroDa);

            // Set relations
            Optional<Entreprise> entreprise = entrepriseRepository.findById(request.getEntrepriseId());
            if (entreprise.isPresent()) {
                demandeAchat.setEntreprise(entreprise.get());
            } else {
                return ResponseEntity.badRequest().build();
            }

            Optional<Site> site = siteRepository.findById(request.getSiteId());
            if (site.isPresent()) {
                demandeAchat.setSite(site.get());
            } else {
                return ResponseEntity.badRequest().build();
            }

            if (request.getDepotId() != null) {
                Optional<Depot> depot = depotRepository.findById(request.getDepotId());
                if (depot.isPresent()) {
                    demandeAchat.setDepot(depot.get());
                }
            }

            // Set default status (BROUILLON)
            Status status = new Status();
            status.setId(1L); // Assuming 1 is BROUILLON
            status.setCode("BROUILLON");
            status.setLibelle("Brouillon");
            demandeAchat.setStatus(status);

            DemandeAchat savedDemandeAchat = demandeAchatService.save(demandeAchat);

            // Save details
            if (request.getDetails() != null) {
                for (CreateDemandeAchatDetailRequest detailRequest : request.getDetails()) {
                    Optional<Article> article = articleRepository.findById(detailRequest.getArticleId());
                    if (article.isPresent()) {
                        DemandeAchatDetails detail = new DemandeAchatDetails();
                        detail.setDemandeAchat(savedDemandeAchat);
                        detail.setArticle(article.get());
                        detail.setQuantiteDemandee(BigDecimal.valueOf(detailRequest.getQuantiteDemandee()));
                        detail.setPrixUnitaire(BigDecimal.valueOf(detailRequest.getPrixUnitaire()));
                        demandeAchatDetailsService.save(detail);
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedDemandeAchat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/demandes")
    public ResponseEntity<List<DemandeAchat>> getAllDemandesAchat() {
        try {
            List<DemandeAchat> demandesAchat = demandeAchatService.findAll();
            if (demandesAchat.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(demandesAchat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/demandes/{id}")
    public ResponseEntity<DemandeAchat> getDemandeAchatById(@PathVariable("id") Long id) {
        Optional<DemandeAchat> demandeAchatData = demandeAchatService.findById(id);

        if (demandeAchatData.isPresent()) {
            return ResponseEntity.ok(demandeAchatData.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/demandes/{id}")
    public ResponseEntity<DemandeAchat> updateDemandeAchat(@PathVariable("id") Long id, @RequestBody DemandeAchat demandeAchat) {
        Optional<DemandeAchat> demandeAchatData = demandeAchatService.findById(id);

        if (demandeAchatData.isPresent()) {
            DemandeAchat _demandeAchat = demandeAchatData.get();
            // Update fields as needed
            _demandeAchat.setNumeroDa(demandeAchat.getNumeroDa());
            _demandeAchat.setDateDemande(demandeAchat.getDateDemande());
            _demandeAchat.setEntreprise(demandeAchat.getEntreprise());
            _demandeAchat.setSite(demandeAchat.getSite());
            _demandeAchat.setDepot(demandeAchat.getDepot());
            _demandeAchat.setDateSouhaitee(demandeAchat.getDateSouhaitee());
            _demandeAchat.setMotifAchat(demandeAchat.getMotifAchat());
            _demandeAchat.setStatus(demandeAchat.getStatus());

            return ResponseEntity.ok(demandeAchatService.save(_demandeAchat));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/demandes/{id}")
    public ResponseEntity<HttpStatus> deleteDemandeAchat(@PathVariable("id") Long id) {
        try {
            demandeAchatService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== GESTION DES DÉTAILS DE DEMANDE D'ACHAT ====================

    @PostMapping("/demandes/{demandeId}/details")
    public ResponseEntity<DemandeAchatDetails> createDemandeAchatDetail(@PathVariable("demandeId") Long demandeId, @RequestBody DemandeAchatDetails demandeAchatDetails) {
        try {
            // Vérifier que la demande d'achat existe
            Optional<DemandeAchat> demandeAchatData = demandeAchatService.findById(demandeId);
            if (!demandeAchatData.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // Associer le détail à la demande d'achat
            demandeAchatDetails.setDemandeAchat(demandeAchatData.get());

            DemandeAchatDetails savedDetail = demandeAchatDetailsService.save(demandeAchatDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDetail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/demandes/{demandeId}/details")
    public ResponseEntity<List<DemandeAchatDetails>> getDetailsByDemandeAchat(@PathVariable("demandeId") Long demandeId) {
        try {
            // Vérifier que la demande d'achat existe
            Optional<DemandeAchat> demandeAchatData = demandeAchatService.findById(demandeId);
            if (!demandeAchatData.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // Pour l'instant, on retourne tous les détails (à améliorer avec une requête personnalisée)
            List<DemandeAchatDetails> allDetails = demandeAchatDetailsService.findAll();
            List<DemandeAchatDetails> filteredDetails = allDetails.stream()
                .filter(detail -> detail.getDemandeAchat().getId().equals(demandeId))
                .toList();

            return ResponseEntity.ok(filteredDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/demandes/details/{detailId}")
    public ResponseEntity<DemandeAchatDetails> getDemandeAchatDetailById(@PathVariable("detailId") Long detailId) {
        Optional<DemandeAchatDetails> detailData = demandeAchatDetailsService.findById(detailId);

        if (detailData.isPresent()) {
            return ResponseEntity.ok(detailData.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/demandes/details/{detailId}")
    public ResponseEntity<DemandeAchatDetails> updateDemandeAchatDetail(@PathVariable("detailId") Long detailId, @RequestBody DemandeAchatDetails demandeAchatDetails) {
        Optional<DemandeAchatDetails> detailData = demandeAchatDetailsService.findById(detailId);

        if (detailData.isPresent()) {
            DemandeAchatDetails _detail = detailData.get();
            // Update fields as needed
            _detail.setArticle(demandeAchatDetails.getArticle());
            _detail.setQuantiteDemandee(demandeAchatDetails.getQuantiteDemandee());
            _detail.setPrixUnitaire(demandeAchatDetails.getPrixUnitaire());

            return ResponseEntity.ok(demandeAchatDetailsService.save(_detail));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/demandes/details/{detailId}")
    public ResponseEntity<HttpStatus> deleteDemandeAchatDetail(@PathVariable("detailId") Long detailId) {
        try {
            demandeAchatDetailsService.deleteById(detailId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}