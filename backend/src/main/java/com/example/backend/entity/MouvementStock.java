package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mouvement_stock")
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement;

    @Column(name = "type_mouvement", nullable = false, length = 20)
    private String typeMouvement;

    @Column(name = "quantite_stock_avant", precision = 15, scale = 2, nullable = false, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal quantiteStockAvant = BigDecimal.ZERO;

    @Column(name = "quantite_entree", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal quantiteEntree = BigDecimal.ZERO;

    @Column(name = "quantite_sortie", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal quantiteSortie = BigDecimal.ZERO;

    @Column(name = "quantite_stock_apres", precision = 15, scale = 2, nullable = false, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal quantiteStockApres = BigDecimal.ZERO;

    @Column(name = "prix_unitaire_mouvement", precision = 15, scale = 2)
    private BigDecimal prixUnitaireMouvement;

    @ManyToOne
    @JoinColumn(name = "id_article", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "id_depot", nullable = false)
    private Depot depot;
}