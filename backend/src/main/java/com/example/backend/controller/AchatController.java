package com.example.backend.controller;

import com.example.backend.entity.DemandeAchat;
import com.example.backend.entity.DemandeAchatDetails;
import com.example.backend.service.DemandeAchatService;
import com.example.backend.service.DemandeAchatDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/achats")
@CrossOrigin(origins = "*")
public class AchatController {

    @Autowired
    private DemandeAchatService demandeAchatService;

    @Autowired
    private DemandeAchatDetailsService demandeAchatDetailsService;

    @PostMapping("/demandes")
    public ResponseEntity<DemandeAchat> createDemandeAchat(@RequestBody DemandeAchat demandeAchat) {
        try {
            DemandeAchat savedDemandeAchat = demandeAchatService.save(demandeAchat);
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