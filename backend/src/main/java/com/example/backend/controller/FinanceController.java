package com.example.backend.controller;

import com.example.backend.entity.Caisse;
import com.example.backend.repository.CaisseRepository;
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

    @GetMapping("/caisses")
    public ResponseEntity<List<Caisse>> getCaisses() {
        List<Caisse> caisses = caisseRepository.findAll();
        return ResponseEntity.ok(caisses);
    }
}
