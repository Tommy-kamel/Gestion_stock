package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "proforma_vente")
public class ProformaVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_proforma", unique = true, nullable = false, length = 50)
    private String numeroProforma;

    @Column(name = "date_proforma")
    private LocalDate dateProforma;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_demande_achat_client", nullable = false)
    private DemandeAchatClient demandeAchatClient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "montant_ttc", precision = 15, scale = 2)
    private BigDecimal montantTtc;

    @OneToMany(mappedBy = "proformaVente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ProformaVenteDetails> details;
}