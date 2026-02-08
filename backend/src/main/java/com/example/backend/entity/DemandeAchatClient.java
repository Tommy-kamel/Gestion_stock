package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "demande_achat_client")
public class DemandeAchatClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_da", unique = true, nullable = false, length = 50)
    private String numeroDa;

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "date_souhaitee")
    private LocalDate dateSouhaitee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "demandeAchatClient", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DemandeAchatClientDetails> details;
}
