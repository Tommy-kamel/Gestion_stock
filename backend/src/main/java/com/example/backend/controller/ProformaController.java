package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.service.ProformaFournisseurService;
import com.example.backend.service.ProformaFournisseurDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/proformas")
@CrossOrigin(origins = "*")
public class ProformaController {

    @Autowired
    private ProformaFournisseurService proformaFournisseurService;

    @Autowired
    private ProformaFournisseurDetailsService proformaFournisseurDetailsService;

    @Autowired
    private com.example.backend.repository.DemandeAchatRepository demandeAchatRepository;

    @Autowired
    private com.example.backend.repository.FournisseurRepository fournisseurRepository;

    @Autowired
    private com.example.backend.repository.ArticleRepository articleRepository;

    @Autowired
    private com.example.backend.repository.StatusRepository statusRepository;

    // DTO pour la création d'un proforma
    public static class CreateProformaRequest {
        private Long demandeAchatId;
        private Long fournisseurId;
        private String numeroProforma;
        private LocalDate dateEmission;
        private Integer dureeValidite; // en jours
        private List<CreateProformaDetailRequest> details;

        // Getters and setters
        public Long getDemandeAchatId() { return demandeAchatId; }
        public void setDemandeAchatId(Long demandeAchatId) { this.demandeAchatId = demandeAchatId; }
        public Long getFournisseurId() { return fournisseurId; }
        public void setFournisseurId(Long fournisseurId) { this.fournisseurId = fournisseurId; }
        public String getNumeroProforma() { return numeroProforma; }
        public void setNumeroProforma(String numeroProforma) { this.numeroProforma = numeroProforma; }
        public LocalDate getDateEmission() { return dateEmission; }
        public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
        public Integer getDureeValidite() { return dureeValidite; }
        public void setDureeValidite(Integer dureeValidite) { this.dureeValidite = dureeValidite; }
        public List<CreateProformaDetailRequest> getDetails() { return details; }
        public void setDetails(List<CreateProformaDetailRequest> details) { this.details = details; }
    }

    public static class CreateProformaDetailRequest {
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

    @PostMapping
    public ResponseEntity<ProformaFournisseur> createProforma(@RequestBody CreateProformaRequest request) {
        try {
            ProformaFournisseur proforma = new ProformaFournisseur();
            
            // Set basic fields
            proforma.setNumeroProforma(request.getNumeroProforma());
            proforma.setDateEmission(request.getDateEmission() != null ? request.getDateEmission() : LocalDate.now());
            
            // Calculate date validité = date emission + durée validité (en jours)
            if (request.getDureeValidite() != null) {
                proforma.setDateValidite(proforma.getDateEmission().plusDays(request.getDureeValidite()));
            }

            // Set relations
            Optional<DemandeAchat> demandeAchat = demandeAchatRepository.findById(request.getDemandeAchatId());
            if (demandeAchat.isPresent()) {
                proforma.setDemandeAchat(demandeAchat.get());
            } else {
                return ResponseEntity.badRequest().build();
            }

            Optional<Fournisseur> fournisseur = fournisseurRepository.findById(request.getFournisseurId());
            if (fournisseur.isPresent()) {
                proforma.setFournisseur(fournisseur.get());
            } else {
                return ResponseEntity.badRequest().build();
            }

            // Set default status (BROUILLON)
            Optional<Status> status = statusRepository.findByCode("BROUILLON");
            if (status.isPresent()) {
                proforma.setStatus(status.get());
            } else {
                Status defaultStatus = new Status();
                defaultStatus.setId(1L);
                defaultStatus.setCode("BROUILLON");
                defaultStatus.setLibelle("Brouillon");
                proforma.setStatus(defaultStatus);
            }

            // Calculate montant TTC
            BigDecimal montantTtc = BigDecimal.ZERO;
            if (request.getDetails() != null) {
                for (CreateProformaDetailRequest detailRequest : request.getDetails()) {
                    if (detailRequest.getPrixUnitaire() != null && detailRequest.getQuantite() != null) {
                        BigDecimal subtotal = detailRequest.getPrixUnitaire().multiply(detailRequest.getQuantite());
                        montantTtc = montantTtc.add(subtotal);
                    }
                }
            }
            proforma.setMontantTtc(montantTtc);

            ProformaFournisseur savedProforma = proformaFournisseurService.save(proforma);

            // Save details
            if (request.getDetails() != null) {
                for (CreateProformaDetailRequest detailRequest : request.getDetails()) {
                    Optional<Article> article = articleRepository.findById(detailRequest.getArticleId());
                    if (article.isPresent()) {
                        ProformaFournisseurDetails detail = new ProformaFournisseurDetails();
                        detail.setProformaFournisseur(savedProforma);
                        detail.setArticle(article.get());
                        detail.setQuantite(detailRequest.getQuantite());
                        detail.setPrixUnitaire(detailRequest.getPrixUnitaire());
                        proformaFournisseurDetailsService.save(detail);
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProforma);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProformaFournisseur>> getAllProformas() {
        try {
            // Ne retourner que les proformas valides
            List<ProformaFournisseur> proformas = proformaFournisseurService.findByStatusCode("VALIDE");
            return ResponseEntity.ok(proformas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProformaFournisseur> getProformaById(@PathVariable("id") Long id) {
        Optional<ProformaFournisseur> proforma = proformaFournisseurService.findById(id);
        return proforma.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/demande/{demandeAchatId}")
    public ResponseEntity<List<ProformaFournisseur>> getProformasByDemandeAchat(@PathVariable("demandeAchatId") Long demandeAchatId) {
        try {
            List<ProformaFournisseur> proformas = proformaFournisseurService.findByDemandeAchatId(demandeAchatId);
            return ResponseEntity.ok(proformas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/demande/{demandeAchatId}/selectionner-meilleur")
    public ResponseEntity<Map<String, Object>> selectionnerMeilleurProforma(@PathVariable("demandeAchatId") Long demandeAchatId) {
        try {
            // Récupérer tous les proformas de cette DA
            List<ProformaFournisseur> proformas = proformaFournisseurService.findByDemandeAchatId(demandeAchatId);
            
            if (proformas.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun proforma trouvé pour cette demande"));
            }

            // Sélectionner le meilleur (le moins cher)
            ProformaFournisseur meilleurProforma = proformaFournisseurService.selectionnerMeilleurProforma(demandeAchatId);
            
            if (meilleurProforma == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Impossible de sélectionner le meilleur proforma"));
            }

            // Mettre à jour les status
            Optional<Status> statusValide = statusRepository.findByCode("VALIDE");
            Optional<Status> statusAnnule = statusRepository.findByCode("ANNULE");

            for (ProformaFournisseur proforma : proformas) {
                if (proforma.getId().equals(meilleurProforma.getId())) {
                    // Marquer comme VALIDE
                    if (statusValide.isPresent()) {
                        proforma.setStatus(statusValide.get());
                    }
                } else {
                    // Marquer comme ANNULE
                    if (statusAnnule.isPresent()) {
                        proforma.setStatus(statusAnnule.get());
                    }
                }
                proformaFournisseurService.save(proforma);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Meilleur proforma sélectionné avec succès");
            response.put("proformaSelectionne", meilleurProforma);
            response.put("montantTtc", meilleurProforma.getMontantTtc());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProforma(@PathVariable("id") Long id) {
        try {
            proformaFournisseurService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
