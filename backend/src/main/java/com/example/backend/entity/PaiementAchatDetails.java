package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "paiement_achat_details")
public class PaiementAchatDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_paiement_achat", nullable = false)
    private PaiementAchat paiementAchat;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montant;

    @Column(name = "reference_externe", length = 100)
    private String referenceExterne;
}