package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "proforma_fournisseur")
public class ProformaFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_proforma", nullable = false, length = 50)
    private String numeroProforma;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @ManyToOne
    @JoinColumn(name = "id_fournisseur", nullable = false)
    private Fournisseur fournisseur;

    @Column(name = "montant_ttc", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal montantTtc = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "id_demande_achat")
    @JsonIgnoreProperties("details")
    private DemandeAchat demandeAchat;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "proformaFournisseur", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ProformaFournisseurDetails> details;
}