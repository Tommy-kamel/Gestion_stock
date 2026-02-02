package com.example.backend.service;

import com.example.backend.entity.DemandeAchat;
import com.example.backend.repository.DemandeAchatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemandeAchatService {

    @Autowired
    private DemandeAchatRepository demandeAchatRepository;

    public List<DemandeAchat> findAll() {
        return demandeAchatRepository.findAll();
    }

    public Optional<DemandeAchat> findById(Long id) {
        return demandeAchatRepository.findById(id);
    }

    public DemandeAchat save(DemandeAchat demandeAchat) {
        return demandeAchatRepository.save(demandeAchat);
    }

    public void deleteById(Long id) {
        demandeAchatRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return demandeAchatRepository.existsById(id);
    }
}