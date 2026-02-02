package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "entreprise")
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 200)
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "id_groupe")
    private Groupe groupe;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
}