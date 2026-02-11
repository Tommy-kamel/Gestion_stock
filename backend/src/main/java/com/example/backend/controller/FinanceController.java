package com.example.backend.controller;

import com.example.backend.entity.Caisse;
import com.example.backend.entity.PaiementAchat;
import com.example.backend.entity.PaiementVente;
import com.example.backend.repository.CaisseRepository;
import com.example.backend.repository.PaiementAchatRepository;
import com.example.backend.repository.PaiementVenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finances")
@CrossOrigin(origins = "*")
public class FinanceController {

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private PaiementAchatRepository paiementAchatRepository;

    @Autowired
    private PaiementVenteRepository paiementVenteRepository;

    @GetMapping("/caisses")
    public ResponseEntity<List<Caisse>> getCaisses() {
        List<Caisse> caisses = caisseRepository.findAll();
        return ResponseEntity.ok(caisses);
    }

    @GetMapping("/paiements-achat")
    public ResponseEntity<List<PaiementAchat>> getPaiementsAchat() {
        List<PaiementAchat> paiements = paiementAchatRepository.findAll();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/paiements-vente")
    public ResponseEntity<List<PaiementVente>> getPaiementsVente() {
        List<PaiementVente> paiements = paiementVenteRepository.findAll();
        return ResponseEntity.ok(paiements);
    }
}
