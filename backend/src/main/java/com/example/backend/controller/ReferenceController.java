package com.example.backend.controller;

import com.example.backend.entity.Client;
import com.example.backend.entity.Entreprise;
import com.example.backend.entity.Fournisseur;
import com.example.backend.entity.Site;
import com.example.backend.repository.ClientRepository;
import com.example.backend.repository.EntrepriseRepository;
import com.example.backend.repository.FournisseurRepository;
import com.example.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/references")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReferenceController {

    private final EntrepriseRepository entrepriseRepository;
    private final SiteRepository siteRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ClientRepository clientRepository;

    @GetMapping("/entreprises")
    public ResponseEntity<List<Entreprise>> getEntreprises() {
        try {
            List<Entreprise> entreprises = entrepriseRepository.findAll();
            return ResponseEntity.ok(entreprises);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sites")
    public ResponseEntity<List<Site>> getSites() {
        try {
            List<Site> sites = siteRepository.findAll();
            return ResponseEntity.ok(sites);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/fournisseurs")
    public ResponseEntity<List<Fournisseur>> getFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
            return ResponseEntity.ok(fournisseurs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/entreprises/clients")
    public ResponseEntity<List<Client>> getClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}