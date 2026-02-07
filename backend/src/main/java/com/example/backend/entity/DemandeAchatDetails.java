package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "demande_achat_details")
public class DemandeAchatDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_demande_achat", nullable = false)
    @JsonIgnoreProperties("details")
    private DemandeAchat demandeAchat;

    @ManyToOne
    @JoinColumn(name = "id_article", nullable = false)
    private Article article;

    @Column(name = "quantite_demandee", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantiteDemandee;

    @Column(name = "prix_unitaire", precision = 15, scale = 2, columnDefinition = "NUMERIC(15,2) DEFAULT 0")
    private BigDecimal prixUnitaire = BigDecimal.ZERO;
}