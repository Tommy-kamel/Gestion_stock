package com.example.backend.service;

import com.example.backend.entity.DemandeAchatDetails;
import com.example.backend.repository.DemandeAchatDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemandeAchatDetailsService {

    @Autowired
    private DemandeAchatDetailsRepository demandeAchatDetailsRepository;

    public List<DemandeAchatDetails> findAll() {
        return demandeAchatDetailsRepository.findAll();
    }

    public Optional<DemandeAchatDetails> findById(Long id) {
        return demandeAchatDetailsRepository.findById(id);
    }

    public DemandeAchatDetails save(DemandeAchatDetails demandeAchatDetails) {
        return demandeAchatDetailsRepository.save(demandeAchatDetails);
    }

    public void deleteById(Long id) {
        demandeAchatDetailsRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return demandeAchatDetailsRepository.existsById(id);
    }
}