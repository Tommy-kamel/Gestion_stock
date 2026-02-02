package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lot_stock")
public class LotStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lot", unique = true, nullable = false, length = 50)
    private String numeroLot;

    @ManyToOne
    @JoinColumn(name = "id_article", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "id_depot", nullable = false)
    private Depot depot;

    @Column(name = "date_entree")
    private LocalDateTime dateEntree;

    @ManyToOne
    @JoinColumn(name = "mouvement_entree_id")
    private MouvementStock mouvementEntree;

    @Column(name = "quantite_initiale", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantiteInitiale;

    @Column(name = "quantite_restante", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantiteRestante;

    @Column(name = "prix_unitaire_achat", precision = 15, scale = 2, nullable = false)
    private BigDecimal prixUnitaireAchat;
}