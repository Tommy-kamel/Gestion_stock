package com.example.backend.controller;

import com.example.backend.entity.DemandeAchat;
import com.example.backend.repository.DemandeAchatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DemandeAchatRepository demandeAchatRepository;

    @GetMapping
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();

        // Compter demandes d'achat
        long totalDemandesAchat = demandeAchatRepository.count();
        List<DemandeAchat> dernieresDemandesAchat = demandeAchatRepository.findAll()
                .stream()
                .sorted((d1, d2) -> d2.getDateDemande().compareTo(d1.getDateDemande()))
                .limit(5)
                .toList();

        data.put("totalDemandesAchat", totalDemandesAchat);
        data.put("demandesEnAttente", 0);
        data.put("totalDevisVente", 0);
        data.put("devisEnAttente", 0);
        data.put("alertesStock", 0);
        data.put("soldeCaisses", BigDecimal.ZERO);
        data.put("creancesClients", BigDecimal.ZERO);
        data.put("dettesFournisseurs", BigDecimal.ZERO);
        data.put("valeurStock", BigDecimal.ZERO);
        data.put("dernieresDemandesAchat", dernieresDemandesAchat);
        data.put("derniersDevis", List.of());

        return data;
    }
}
