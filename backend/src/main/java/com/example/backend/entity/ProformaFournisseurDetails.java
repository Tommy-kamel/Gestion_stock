package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "proforma_fournisseur_details")
public class ProformaFournisseurDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_proforma_fournisseur", nullable = false)
    private ProformaFournisseur proformaFournisseur;

    @ManyToOne
    @JoinColumn(name = "id_article", nullable = false)
    private Article article;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire", precision = 15, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;
}