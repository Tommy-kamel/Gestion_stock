package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "facture_vente")
public class FactureVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", unique = true, nullable = false, length = 50)
    private String numeroFacture;

    @Column(name = "date_facture")
    private LocalDate dateFacture;

    @ManyToOne
    @JoinColumn(name = "id_bon_commande_vente")
    private BonCommandeVente bonCommandeVente;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "montant_ttc", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTtc;

    @Column(name = "reste_a_payer", precision = 15, scale = 2, nullable = false)
    private BigDecimal resteAPayer;
}