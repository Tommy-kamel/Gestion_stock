package com.example.backend.controller;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private DemandeAchatRepository demandeAchatRepository;
    @Autowired private ProformaFournisseurRepository proformaFournisseurRepository;
    @Autowired private BonCommandeAchatRepository bonCommandeAchatRepository;
    @Autowired private DemandeAchatClientRepository demandeAchatClientRepository;
    @Autowired private ProformaVenteRepository proformaVenteRepository;
    @Autowired private BonCommandeVenteRepository bonCommandeVenteRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private DepotRepository depotRepository;
    @Autowired private MouvementStockRepository mouvementStockRepository;
    @Autowired private FournisseurRepository fournisseurRepository;
    @Autowired private ClientRepository clientRepository;

    @GetMapping("/achats")
    public Map<String, Object> getDashboardAchats() {
        Map<String, Object> data = new HashMap<>();

        // Statistiques générales
        long totalDemandesAchat = demandeAchatRepository.count();
        long totalProformas = proformaFournisseurRepository.count();
        long totalBonCommande = bonCommandeAchatRepository.count();
        
        // Demandes par statut
        List<DemandeAchat> demandes = demandeAchatRepository.findAll();
        Map<String, Long> demandesParStatut = demandes.stream()
            .collect(Collectors.groupingBy(d -> d.getStatus().getLibelle(), Collectors.counting()));
        
        // Montant total des bons de commande
        BigDecimal montantTotal = bonCommandeAchatRepository.findAll().stream()
            .map(bc -> bc.getMontantTtc() != null ? bc.getMontantTtc() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Top 5 fournisseurs (par nombre de proformas)
        Map<String, Long> proformasParFournisseur = proformaFournisseurRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                p -> p.getFournisseur().getNom(),
                Collectors.counting()
            ));
        List<Map<String, Object>> topFournisseurs = proformasParFournisseur.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("nom", e.getKey());
                map.put("count", e.getValue());
                return map;
            })
            .collect(Collectors.toList());
        
        // Dernières demandes
        List<DemandeAchat> dernieresDemandesAchat = demandes.stream()
            .sorted((d1, d2) -> d2.getDateDemande().compareTo(d1.getDateDemande()))
            .limit(5)
            .collect(Collectors.toList());

        data.put("totalDemandesAchat", totalDemandesAchat);
        data.put("totalProformas", totalProformas);
        data.put("totalBonCommande", totalBonCommande);
        data.put("montantTotal", montantTotal);
        data.put("demandesParStatut", demandesParStatut);
        data.put("topFournisseurs", topFournisseurs);
        data.put("dernieresDemandesAchat", dernieresDemandesAchat);

        return data;
    }

    @GetMapping("/ventes")
    public Map<String, Object> getDashboardVentes() {
        Map<String, Object> data = new HashMap<>();

        // Statistiques générales
        long totalDemandesClient = demandeAchatClientRepository.count();
        long totalProformas = proformaVenteRepository.count();
        long totalBonCommande = bonCommandeVenteRepository.count();
        
        // Proformas par statut
        List<ProformaVente> proformas = proformaVenteRepository.findAll();
        Map<String, Long> proformasParStatut = proformas.stream()
            .collect(Collectors.groupingBy(p -> p.getStatus().getLibelle(), Collectors.counting()));
        
        // Chiffre d'affaires (montant total des BC vente)
        BigDecimal chiffreAffaires = bonCommandeVenteRepository.findAll().stream()
            .map(bc -> bc.getMontantTtc() != null ? bc.getMontantTtc() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Top 5 clients (par nombre de proformas)
        Map<String, Long> proformasParClient = proformas.stream()
            .filter(p -> p.getDemandeAchatClient() != null && p.getDemandeAchatClient().getClient() != null)
            .collect(Collectors.groupingBy(
                p -> p.getDemandeAchatClient().getClient().getNom(),
                Collectors.counting()
            ));
        List<Map<String, Object>> topClients = proformasParClient.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("nom", e.getKey());
                map.put("count", e.getValue());
                return map;
            })
            .collect(Collectors.toList());
        
        // Dernières demandes clients
        List<DemandeAchatClient> dernieresDemandesClient = demandeAchatClientRepository.findAll().stream()
            .sorted((d1, d2) -> d2.getDateDemande().compareTo(d1.getDateDemande()))
            .limit(5)
            .collect(Collectors.toList());

        data.put("totalDemandesClient", totalDemandesClient);
        data.put("totalProformas", totalProformas);
        data.put("totalBonCommande", totalBonCommande);
        data.put("chiffreAffaires", chiffreAffaires);
        data.put("proformasParStatut", proformasParStatut);
        data.put("topClients", topClients);
        data.put("dernieresDemandesClient", dernieresDemandesClient);

        return data;
    }

    @GetMapping("/stock")
    public Map<String, Object> getDashboardStock() {
        Map<String, Object> data = new HashMap<>();

        // Statistiques générales
        long totalArticles = articleRepository.count();
        long totalDepots = depotRepository.count();
        long totalMouvements = mouvementStockRepository.count();
        
        // Valeur totale du stock (somme de quantité * prix pour tous les articles)
        List<Stock> stocks = stockRepository.findAll();
        BigDecimal valeurTotaleStock = stocks.stream()
            .map(s -> {
                BigDecimal qty = s.getQuantiteActuelle() != null ? s.getQuantiteActuelle() : BigDecimal.ZERO;
                BigDecimal prix = s.getArticle().getPrixAchatRef() != null ? s.getArticle().getPrixAchatRef() : BigDecimal.ZERO;
                return qty.multiply(prix);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Articles en rupture (quantité <= 0)
        long articlesEnRupture = stocks.stream()
            .filter(s -> s.getQuantiteActuelle().compareTo(BigDecimal.ZERO) <= 0)
            .count();
        
        // Top 5 articles les plus valorisés
        List<Map<String, Object>> topArticles = stocks.stream()
            .map(s -> {
                BigDecimal qty = s.getQuantiteActuelle() != null ? s.getQuantiteActuelle() : BigDecimal.ZERO;
                BigDecimal prix = s.getArticle().getPrixAchatRef() != null ? s.getArticle().getPrixAchatRef() : BigDecimal.ZERO;
                BigDecimal valeur = qty.multiply(prix);
                Map<String, Object> item = new HashMap<>();
                item.put("nom", s.getArticle().getDesignation());
                item.put("quantite", qty);
                item.put("valeur", valeur);
                return item;
            })
            .sorted((a, b) -> ((BigDecimal)b.get("valeur")).compareTo((BigDecimal)a.get("valeur")))
            .limit(5)
            .collect(Collectors.toList());
        
        // Mouvements par type (ENTREE/SORTIE)
        List<MouvementStock> mouvements = mouvementStockRepository.findAll();
        Map<String, Long> mouvementsParType = mouvements.stream()
            .collect(Collectors.groupingBy(MouvementStock::getTypeMouvement, Collectors.counting()));
        
        // Derniers mouvements
        List<MouvementStock> derniersMouvements = mouvements.stream()
            .sorted((m1, m2) -> m2.getDateMouvement().compareTo(m1.getDateMouvement()))
            .limit(10)
            .collect(Collectors.toList());

        data.put("totalArticles", totalArticles);
        data.put("totalDepots", totalDepots);
        data.put("totalMouvements", totalMouvements);
        data.put("valeurTotaleStock", valeurTotaleStock);
        data.put("articlesEnRupture", articlesEnRupture);
        data.put("topArticles", topArticles);
        data.put("mouvementsParType", mouvementsParType);
        data.put("derniersMouvements", derniersMouvements);

        return data;
    }

    @GetMapping
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();

        // Vue globale synthétique
        data.put("totalDemandesAchat", demandeAchatRepository.count());
        data.put("totalProformasAchat", proformaFournisseurRepository.count());
        data.put("totalProformasVente", proformaVenteRepository.count());
        data.put("totalArticles", articleRepository.count());
        data.put("totalFournisseurs", fournisseurRepository.count());
        data.put("totalClients", clientRepository.count());

        return data;
    }
}
