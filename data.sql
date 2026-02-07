-- Donnees d'exemple pour la base de donnees gestion_stock
-- À executer apres avoir cree les tables avec base.sql
-- Encodage UTF-8 pour eviter les problemes de caracteres accentues

-- ------------------------------------------------------------------------------
-- 1. STATUTS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO utilisateur (nom, mdp) VALUES
('admin', 'admin');

INSERT INTO status (code, libelle, niveau) VALUES
('BROUILLON', 'Brouillon', 1),
('EN_COURS', 'En cours', 2),
('VALIDE', 'Valide', 3),
('ANNULE', 'Annule', 4),
('CLOTURE', 'Cloture', 5);

-- ------------------------------------------------------------------------------
-- 2. UNITeS DE MESURE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO unite (code, libelle) VALUES
('KG', 'Kilogramme'),
('L', 'Litre'),
('M', 'Metre'),
('M2', 'Metre carre'),
('M3', 'Metre cube'),
('U', 'Unite');

-- ------------------------------------------------------------------------------
-- 3. CATeGORIES D'ARTICLES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO article_categorie (code, libelle) VALUES
('MAT_PREMIER', 'Matieres premieres'),
('PRODUIT_FINI', 'Produits finis'),
('CONSOMMABLE', 'Consommables'),
('EQUIPEMENT', 'equipements'),
('SERVICE', 'Services');

-- ------------------------------------------------------------------------------
-- 4. MeTHODES DE VALORISATION STOCK (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO methode_valorisation_stock (code, libelle, description) VALUES
('FIFO', 'First In First Out', 'Premier entre, premier sorti'),
('LIFO', 'Last In First Out', 'Dernier entre, premier sorti'),
('CMUP', 'Coût Moyen Unitaire Pondere', 'Moyenne ponderee des coûts');

-- ------------------------------------------------------------------------------
-- 5. GROUPES D'ENTREPRISES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO groupe (nom, description) VALUES
('Groupe Industriel ABC', 'Groupe specialise dans l''industrie manufacturiere'),
('Groupe Commercial XYZ', 'Groupe axe sur le commerce et la distribution');

-- ------------------------------------------------------------------------------
-- 6. ENTREPRISES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO entreprise (nom, adresse, id_groupe) VALUES
('Entreprise A', '123 Rue de l''Industrie, Ville A', 1),
('Entreprise B', '456 Avenue du Commerce, Ville B', 2);

-- ------------------------------------------------------------------------------
-- 7. SITES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO site (nom, adresse, telephone, email, id_entreprise) VALUES
('Site Principal A', '123 Rue de l''Industrie, Ville A', '+33123456789', 'contact@entreprisea.com', 1),
('Site Secondaire A', '789 Boulevard Industriel, Ville A', '+33123456790', 'site2@entreprisea.com', 1),
('Site Principal B', '456 Avenue du Commerce, Ville B', '+33987654321', 'contact@entrepriseb.com', 2);

-- ------------------------------------------------------------------------------
-- 8. DePoTS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO depot (nom, adresse, id_site, id_methode_valorisation_stock) VALUES
('Depot Principal A', '123 Rue de l''Industrie, Ville A', 1, 1),
('Depot Secondaire A', '789 Boulevard Industriel, Ville A', 2, 3),
('Depot Principal B', '456 Avenue du Commerce, Ville B', 3, 2);

-- ------------------------------------------------------------------------------
-- 9. FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO fournisseur (nom, adresse) VALUES
('Fournisseur Alpha', '10 Rue des Fournisseurs, Ville F1'),
('Fournisseur Beta', '20 Avenue Fournisseur, Ville F2'),
('Fournisseur Gamma', '30 Boulevard Fournisseur, Ville F3');

-- ------------------------------------------------------------------------------
-- 10. CLIENTS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO client (nom, adresse) VALUES
('Client Premium', '100 Rue des Clients, Ville C1'),
('Client Standard', '200 Avenue Client, Ville C2');

-- ------------------------------------------------------------------------------
-- 11. ARTICLES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO article (reference, designation, prix_achat_ref, prix_vente_ref, id_unite, article_categorie_id) VALUES
('ART001', 'Acier inoxydable 304', 25.50, 35.00, 1, 1),
('ART002', 'Huile moteur synthetique', 45.00, 65.00, 2, 1),
('ART003', 'Tuyau PVC 50mm', 8.50, 12.00, 3, 1),
('ART004', 'Peinture acrylique blanche 5L', 15.00, 22.00, 2, 1),
('ART005', 'Produit fini - Moteur electrique', 120.00, 180.00, 6, 2),
('ART006', 'Consommable - Gants de protection', 2.50, 4.00, 6, 3),
('ART007', 'equipement - Tour CNC', 2500.00, 3500.00, 6, 4);

-- ------------------------------------------------------------------------------
-- 12. STOCK INITIAL (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO stock (article_id, depot_id, quantite_actuelle) VALUES
(1, 1, 500.00),
(2, 1, 200.00),
(3, 2, 1000.00),
(4, 2, 150.00),
(5, 3, 50.00),
(6, 1, 1000.00),
(7, 3, 5.00);

-- ------------------------------------------------------------------------------
-- 13. MOUVEMENTS DE STOCK INITIAUX (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO mouvement_stock (type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
('ENTREE', 0.00, 500.00, 0.00, 500.00, 25.50, 1, 1),
('ENTREE', 0.00, 200.00, 0.00, 200.00, 45.00, 2, 1),
('ENTREE', 0.00, 1000.00, 0.00, 1000.00, 8.50, 3, 2),
('ENTREE', 0.00, 150.00, 0.00, 150.00, 15.00, 4, 2),
('ENTREE', 0.00, 50.00, 0.00, 50.00, 120.00, 5, 3),
('ENTREE', 0.00, 1000.00, 0.00, 1000.00, 2.50, 6, 1),
('ENTREE', 0.00, 5.00, 0.00, 5.00, 2500.00, 7, 3);

-- ------------------------------------------------------------------------------
-- 14. LOTS DE STOCK (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat) VALUES
('LOT-001-2024', 1, 1, 1, 500.00, 500.00, 25.50),
('LOT-002-2024', 2, 1, 2, 200.00, 200.00, 45.00),
('LOT-003-2024', 3, 2, 3, 1000.00, 1000.00, 8.50),
('LOT-004-2024', 4, 2, 4, 150.00, 150.00, 15.00),
('LOT-005-2024', 5, 3, 5, 50.00, 50.00, 120.00),
('LOT-006-2024', 6, 1, 6, 1000.00, 1000.00, 2.50),
('LOT-007-2024', 7, 3, 7, 5.00, 5.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 15. DEMANDES D'ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-001', '2024-01-15', 1, 1, 1, '2024-02-15', 'Reapprovisionnement matieres premieres', 3),
('DA-2024-002', '2024-01-20', 2, 2, 2, '2024-02-20', 'equipement production', 3);

-- ------------------------------------------------------------------------------
-- 16. DeTAILS DEMANDES D'ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(1, 1, 300.00, 25.50),
(1, 2, 100.00, 45.00),
(2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 17. PROFORMAS FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat) VALUES
('PF-2024-001', '2024-01-16', '2024-02-16', 1, 12750.00, 1),
('PF-2024-002', '2024-01-21', '2024-02-21', 3, 5000.00, 2);

-- ------------------------------------------------------------------------------
-- 18. DeTAILS PROFORMAS FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(1, 1, 300.00, 25.50),
(1, 2, 100.00, 45.00),
(2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 19. BONS DE COMMANDE ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO bon_commande_achat (numero_bc, date_commande, id_proforma_fournisseur, id_status, montant_ttc) VALUES
('BCA-2024-001', '2024-01-18', 1, 3, 12750.00),
('BCA-2024-002', '2024-01-23', 2, 3, 5000.00);

-- ------------------------------------------------------------------------------
-- 20. DeTAILS BONS DE COMMANDE ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO bon_commande_achat_details (id_bon_commande_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 300.00, 25.50),
(1, 2, 100.00, 45.00),
(2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 21. FACTURES ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO facture_achat (numero_facture_fournisseur, date_facture, id_bon_commande_achat, id_status, id_client, montant_ttc, reste_a_payer) VALUES
('FA-2024-001', '2024-02-01', 1, 3, 1, 12750.00, 0.00),
('FA-2024-002', '2024-02-05', 2, 3, 2, 5000.00, 0.00);

-- ------------------------------------------------------------------------------
-- 22. DeTAILS FACTURES ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO facture_achat_details (id_facture_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 300.00, 25.50),
(1, 2, 100.00, 45.00),
(2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 23. PROFORMAS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO proforma_vente (numero_proforma, date_proforma, id_client, id_status, montant_ttc) VALUES
('PV-2024-001', '2024-01-10', 1, 3, 4000.00),
('PV-2024-002', '2024-01-12', 2, 3, 9000.00);

-- ------------------------------------------------------------------------------
-- 24. DeTAILS PROFORMAS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO proforma_vente_details (id_proforma_vente, id_article, quantite, prix_unitaire) VALUES
(1, 5, 20.00, 180.00),
(1, 6, 200.00, 4.00),
(2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 25. BONS DE COMMANDE VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO bon_commande_vente (numero_bc, date_commande, id_proforma_vente, id_client, id_status, montant_ttc) VALUES
('BCV-2024-001', '2024-01-12', 1, 1, 3, 4000.00),
('BCV-2024-002', '2024-01-14', 2, 2, 3, 9000.00);

-- ------------------------------------------------------------------------------
-- 26. DeTAILS BONS DE COMMANDE VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO bon_commande_vente_details (id_bon_commande_vente, id_article, quantite, prix_unitaire) VALUES
(1, 5, 20.00, 180.00),
(1, 6, 200.00, 4.00),
(2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 27. FACTURES VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO facture_vente (numero_facture, date_facture, id_bon_commande_vente, id_status, montant_ttc, reste_a_payer) VALUES
('FV-2024-001', '2024-01-20', 1, 3, 4000.00, 0.00),
('FV-2024-002', '2024-01-22', 2, 3, 9000.00, 0.00);

-- ------------------------------------------------------------------------------
-- 28. DeTAILS FACTURES VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO facture_vente_details (id_facture_vente, id_article, quantite, prix_unitaire) VALUES
(1, 5, 20.00, 180.00),
(1, 6, 200.00, 4.00),
(2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 29. CAISSES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO caisse (code_caisse, libelle, solde_actuel, entreprise_id) VALUES
('CAISSE_A', 'Caisse Entreprise A', 10000.00, 1),
('CAISSE_B', 'Caisse Entreprise B', 5000.00, 2);

-- ------------------------------------------------------------------------------
-- 30. MOUVEMENTS CAISSE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO caisse_mouvement (libelle_operation, montant_entree, montant_sortie, solde_avant, solde_apres, id_caisse) VALUES
('Initialisation caisse', 10000.00, 0.00, 0.00, 10000.00, 1),
('Initialisation caisse', 5000.00, 0.00, 0.00, 5000.00, 2),
('Paiement achat', 0.00, 12750.00, 10000.00, -2750.00, 1),
('Paiement achat', 0.00, 5000.00, 5000.00, 0.00, 2),
('Paiement vente', 4000.00, 0.00, -2750.00, 1250.00, 1),
('Paiement vente', 9000.00, 0.00, 0.00, 9000.00, 2);

-- ------------------------------------------------------------------------------
-- 31. PAIEMENTS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO paiement_vente (numero_recu, date_paiement, id_facture_vente, id_caisse_mouvement, montant_total_paye) VALUES
('REC-2024-001', '2024-01-21', 1, 5, 4000.00),
('REC-2024-002', '2024-01-23', 2, 6, 9000.00);

-- ------------------------------------------------------------------------------
-- 32. DeTAILS PAIEMENTS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO paiement_vente_details (id_paiement_vente, montant) VALUES
(1, 4000.00),
(2, 9000.00);

-- ------------------------------------------------------------------------------
-- 33. PAIEMENTS ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO paiement_achat (numero_paiement, date_paiement, id_facture_achat, id_caisse_mouvement, montant_total_paye) VALUES
('PAI-2024-001', '2024-02-03', 1, 3, 12750.00),
('PAI-2024-002', '2024-02-07', 2, 4, 5000.00);

-- ------------------------------------------------------------------------------
-- 34. DeTAILS PAIEMENTS ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO paiement_achat_details (id_paiement_achat, montant, reference_externe) VALUES
(1, 12750.00, 'VIR-2024-001'),
(2, 5000.00, 'CHQ-2024-001');