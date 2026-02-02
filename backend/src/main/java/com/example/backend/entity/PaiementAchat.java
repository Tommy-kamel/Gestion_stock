package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "paiement_achat")
public class PaiementAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_paiement", unique = true, length = 50)
    private String numeroPaiement;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @ManyToOne
    @JoinColumn(name = "id_facture_achat", nullable = false)
    private FactureAchat factureAchat;

    @ManyToOne
    @JoinColumn(name = "id_caisse_mouvement", nullable = false)
    private CaisseMouvement caisseMouvement;

    @Column(name = "montant_total_paye", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTotalPaye;
}