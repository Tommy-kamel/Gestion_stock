package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/meta")
@CrossOrigin(origins = "*")
public class MetaController {

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpoints() {
        Map<String, Object> response = new HashMap<>();
        
        // ACHATS
        List<Map<String, String>> achats = new ArrayList<>();
        achats.add(Map.of("method", "GET", "path", "/api/achats/demandes", "description", "Liste toutes les demandes d'achat"));
        achats.add(Map.of("method", "GET", "path", "/api/achats/proformas", "description", "Liste tous les proformas fournisseurs"));
        achats.add(Map.of("method", "GET", "path", "/api/bons-commande-achat", "description", "Liste tous les bons de commande achat"));
        achats.add(Map.of("method", "GET", "path", "/api/proformas", "description", "Liste toutes les offres fournisseurs (proformas)"));
        
        // VENTES
        List<Map<String, String>> ventes = new ArrayList<>();
        ventes.add(Map.of("method", "GET", "path", "/api/ventes/demandes-client", "description", "Liste toutes les demandes clients"));
        ventes.add(Map.of("method", "GET", "path", "/api/ventes/proformas", "description", "Liste tous les proformas vente (devis clients)"));
        ventes.add(Map.of("method", "GET", "path", "/api/ventes/proformas/valides", "description", "Proformas vente validés"));
        ventes.add(Map.of("method", "GET", "path", "/api/ventes/bons-commande", "description", "Liste tous les bons de commande vente"));
        
        // STOCK
        List<Map<String, String>> stock = new ArrayList<>();
        stock.add(Map.of("method", "GET", "path", "/api/stock/valorisation", "description", "État du stock valorisé avec mouvements et valeurs"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/articles", "description", "Liste tous les articles du catalogue"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/depots", "description", "Liste tous les dépôts d'entreposage"));
        
        // RÉFÉRENTIEL
        List<Map<String, String>> referentiel = new ArrayList<>();
        referentiel.add(Map.of("method", "GET", "path", "/api/references/entreprises", "description", "Liste toutes les entreprises"));
        referentiel.add(Map.of("method", "GET", "path", "/api/references/sites", "description", "Liste tous les sites"));
        referentiel.add(Map.of("method", "GET", "path", "/api/references/fournisseurs", "description", "Liste tous les fournisseurs"));
        referentiel.add(Map.of("method", "GET", "path", "/api/references/entreprises/clients", "description", "Liste tous les clients"));
        
        // FINANCES
        List<Map<String, String>> finances = new ArrayList<>();
        finances.add(Map.of("method", "GET", "path", "/api/finances/caisses", "description", "Liste toutes les caisses avec soldes"));
        finances.add(Map.of("method", "GET", "path", "/api/finances/paiements-achat", "description", "Liste tous les paiements fournisseurs"));
        finances.add(Map.of("method", "GET", "path", "/api/finances/paiements-vente", "description", "Liste tous les encaissements clients"));
        
        // DASHBOARD
        List<Map<String, String>> dashboard = new ArrayList<>();
        dashboard.add(Map.of("method", "GET", "path", "/api/dashboard/synthese", "description", "Vue synthétique globale (KPIs, stats)"));
        
        response.put("achats", achats);
        response.put("ventes", ventes);
        response.put("stock", stock);
        response.put("referentiel", referentiel);
        response.put("finances", finances);
        response.put("dashboard", dashboard);
        
        return ResponseEntity.ok(response);
    }
}
