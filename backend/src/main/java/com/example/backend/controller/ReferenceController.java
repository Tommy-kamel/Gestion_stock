package com.example.backend.controller;

import com.example.backend.entity.Entreprise;
import com.example.backend.entity.Site;
import com.example.backend.repository.EntrepriseRepository;
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
}