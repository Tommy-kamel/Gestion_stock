package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "demande_achat")
public class DemandeAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_da", unique = true, nullable = false, length = 50)
    private String numeroDa;

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_site", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_depot")
    private Depot depot;

    @Column(name = "date_souhaitee")
    private LocalDate dateSouhaitee;

    @Column(name = "motif_achat", columnDefinition = "TEXT")
    private String motifAchat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "demandeAchat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DemandeAchatDetails> details;
}