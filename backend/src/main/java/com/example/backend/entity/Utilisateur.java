package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", unique = true, nullable = false, length = 50)
    private String nom;

    @Column(name = "mdp", nullable = false, length = 100)
    private String mdp;
}