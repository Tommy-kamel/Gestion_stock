package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "methode_valorisation_stock")
public class MethodeValorisationStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(length = 100)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;
}