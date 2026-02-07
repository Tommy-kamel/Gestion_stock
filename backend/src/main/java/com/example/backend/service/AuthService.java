package com.example.backend.service;

import com.example.backend.entity.Utilisateur;
import com.example.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    public Optional<Utilisateur> authenticate(String username, String password) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByNom(username);

        if (utilisateur.isPresent() && utilisateur.get().getMdp().equals(password)) {
            return utilisateur;
        }

        return Optional.empty();
    }

    public Utilisateur createUser(String nom, String mdp) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setMdp(mdp);
        return utilisateurRepository.save(utilisateur);
    }
}