package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "depot")
public class Depot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 200)
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "id_site", nullable = false)
    private Site site;

    @ManyToOne
    @JoinColumn(name = "id_methode_valorisation_stock")
    private MethodeValorisationStock methodeValorisationStock;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
}