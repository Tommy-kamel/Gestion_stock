package com.example.backend.service;

import com.example.backend.entity.ProformaFournisseur;
import com.example.backend.repository.ProformaFournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProformaFournisseurService {

    @Autowired
    private ProformaFournisseurRepository proformaFournisseurRepository;

    public List<ProformaFournisseur> findAll() {
        return proformaFournisseurRepository.findAll();
    }

    public List<ProformaFournisseur> findByDemandeAchatId(Long demandeAchatId) {
        return proformaFournisseurRepository.findByDemandeAchatId(demandeAchatId);
    }

    public List<ProformaFournisseur> findByStatusCode(String statusCode) {
        return proformaFournisseurRepository.findByStatusCode(statusCode);
    }

    public Optional<ProformaFournisseur> findById(Long id) {
        return proformaFournisseurRepository.findById(id);
    }

    public ProformaFournisseur save(ProformaFournisseur proformaFournisseur) {
        return proformaFournisseurRepository.save(proformaFournisseur);
    }

    public void deleteById(Long id) {
        proformaFournisseurRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return proformaFournisseurRepository.existsById(id);
    }

    /**
     * Sélectionne le meilleur proforma (le moins cher) pour une demande d'achat
     * @param demandeAchatId ID de la demande d'achat
     * @return Le proforma sélectionné ou null si aucun proforma trouvé
     */
    public ProformaFournisseur selectionnerMeilleurProforma(Long demandeAchatId) {
        List<ProformaFournisseur> proformas = findByDemandeAchatId(demandeAchatId);
        
        if (proformas.isEmpty()) {
            return null;
        }

        // Trouver le proforma avec le montant TTC le plus bas
        Optional<ProformaFournisseur> meilleurProforma = proformas.stream()
            .min(Comparator.comparing(ProformaFournisseur::getMontantTtc));

        return meilleurProforma.orElse(null);
    }
}
