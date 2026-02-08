package com.example.backend.service;

import com.example.backend.entity.ProformaFournisseurDetails;
import com.example.backend.repository.ProformaFournisseurDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProformaFournisseurDetailsService {

    @Autowired
    private ProformaFournisseurDetailsRepository proformaFournisseurDetailsRepository;

    public List<ProformaFournisseurDetails> findAll() {
        return proformaFournisseurDetailsRepository.findAll();
    }

    public Optional<ProformaFournisseurDetails> findById(Long id) {
        return proformaFournisseurDetailsRepository.findById(id);
    }

    public ProformaFournisseurDetails save(ProformaFournisseurDetails proformaFournisseurDetails) {
        return proformaFournisseurDetailsRepository.save(proformaFournisseurDetails);
    }

    public void deleteById(Long id) {
        proformaFournisseurDetailsRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return proformaFournisseurDetailsRepository.existsById(id);
    }
}
