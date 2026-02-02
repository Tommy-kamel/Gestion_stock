package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "sortie_lot_detail")
public class SortieLotDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mouvement_sortie_id", nullable = false)
    private MouvementStock mouvementSortie;

    @ManyToOne
    @JoinColumn(name = "lot_stock_id", nullable = false)
    private LotStock lotStock;

    @Column(name = "quantite_prelevee", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantitePrelevee;

    @Column(name = "prix_unitaire_lot", precision = 15, scale = 2, nullable = false)
    private BigDecimal prixUnitaireLot;
}