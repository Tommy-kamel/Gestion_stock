package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "paiement_vente")
public class PaiementVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_recu", unique = true, length = 50)
    private String numeroRecu;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @ManyToOne
    @JoinColumn(name = "id_facture_vente", nullable = false)
    private FactureVente factureVente;

    @ManyToOne
    @JoinColumn(name = "id_caisse_mouvement", nullable = false)
    private CaisseMouvement caisseMouvement;

    @Column(name = "montant_total_paye", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTotalPaye;
}