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
        ventes.add(Map.of("method", "GET", "path", "/api/ventes/demandes-client/validees", "description", "Demandes clients validées"));
        
        // STOCK
        List<Map<String, String>> stock = new ArrayList<>();
        stock.add(Map.of("method", "GET", "path", "/api/stock/valorisation", "description", "Valeur totale du stock avec détails par article"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/articles", "description", "Liste complète des articles du catalogue"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/depots", "description", "Liste tous les dépôts de stockage"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/mouvements", "description", "Liste TOUS les mouvements de stock (entrées/sorties) - utiliser pour compter"));
        stock.add(Map.of("method", "GET", "path", "/api/stock/lots", "description", "Liste tous les lots de stock avec traçabilité"));
        
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
        dashboard.add(Map.of("method", "GET", "path", "/api/dashboard/achats", "description", "Stats Achats: total demandes, proformas, BC, montant, top fournisseurs"));
        dashboard.add(Map.of("method", "GET", "path", "/api/dashboard/ventes", "description", "Stats Ventes: total demandes clients, proformas, BC, CA, top clients"));
        dashboard.add(Map.of("method", "GET", "path", "/api/dashboard/stock", "description", "Stats Stock: total articles/dépôts/mouvements, valeur, ruptures, top articles"));
        
        response.put("achats", achats);
        response.put("ventes", ventes);
        response.put("stock", stock);
        response.put("referentiel", referentiel);
        response.put("finances", finances);
        response.put("dashboard", dashboard);
        
        return ResponseEntity.ok(response);
    }
}
