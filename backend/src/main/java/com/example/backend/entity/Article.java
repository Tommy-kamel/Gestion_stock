package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(name = "prix_achat_ref", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal prixAchatRef = BigDecimal.ZERO;

    @Column(name = "prix_vente_ref", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal prixVenteRef = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_unite", nullable = false)
    private Unite unite;

    @ManyToOne
    @JoinColumn(name = "article_categorie_id", nullable = false)
    private ArticleCategorie articleCategorie;
}