package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "paiement_vente_details")
public class PaiementVenteDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_paiement_vente", nullable = false)
    private PaiementVente paiementVente;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montant;
}