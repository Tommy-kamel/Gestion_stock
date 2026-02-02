package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "facture_achat")
public class FactureAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture_fournisseur", nullable = false, length = 50)
    private String numeroFactureFournisseur;

    @Column(name = "date_facture")
    private LocalDate dateFacture;

    @ManyToOne
    @JoinColumn(name = "id_bon_commande_achat")
    private BonCommandeAchat bonCommandeAchat;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "montant_ttc", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTtc;

    @Column(name = "reste_a_payer", precision = 15, scale = 2, nullable = false)
    private BigDecimal resteAPayer;
}