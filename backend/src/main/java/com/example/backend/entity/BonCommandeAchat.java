package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "bon_commande_achat")
public class BonCommandeAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_bc", unique = true, nullable = false, length = 50)
    private String numeroBc;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proforma_fournisseur")
    @JsonIgnoreProperties({"demandeAchat", "details"})
    private ProformaFournisseur proformaFournisseur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "montant_ttc", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTtc;
}