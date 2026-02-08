package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proforma_vente")
    private ProformaVente proformaVente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "montant_ttc", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTtc;

    @OneToMany(mappedBy = "bonCommandeVente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BonCommandeVenteDetails> details;
}