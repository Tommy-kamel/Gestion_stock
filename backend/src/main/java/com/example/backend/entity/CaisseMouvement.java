package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "caisse_mouvement")
public class CaisseMouvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement;

    @Column(name = "libelle_operation", length = 200)
    private String libelleOperation;

    @Column(name = "montant_entree", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal montantEntree = BigDecimal.ZERO;

    @Column(name = "montant_sortie", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal montantSortie = BigDecimal.ZERO;

    @Column(name = "solde_avant", precision = 15, scale = 2, nullable = false)
    private BigDecimal soldeAvant;

    @Column(name = "solde_apres", precision = 15, scale = 2, nullable = false)
    private BigDecimal soldeApres;

    @ManyToOne
    @JoinColumn(name = "id_caisse", nullable = false)
    private Caisse caisse;
}