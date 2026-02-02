package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "bon_commande_vente")
public class BonCommandeVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_bc", unique = true, nullable = false, length = 50)
    private String numeroBc;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @ManyToOne
    @JoinColumn(name = "id_proforma_vente")
    private ProformaVente proformaVente;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "montant_ttc", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTtc;
}