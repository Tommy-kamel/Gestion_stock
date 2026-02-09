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
('SERVICE', 'Services'),
('ELECTRONIQUE', 'Articles électroniques');

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
('Groupe Industriel ABC', 'Groupe specialise dans l''industrie manufacturiere');
-- ------------------------------------------------------------------------------
-- 6. ENTREPRISES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO entreprise (nom, adresse, id_groupe) VALUES
('SARL Tananarive Distribution', 'Lot II C 123, Antananarivo', 1),
('Madagascar Agro Industrie', 'Zone Industrielle, Toamasina', 1);

-- ------------------------------------------------------------------------------
-- 7. SITES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO site (nom, adresse, telephone, email, id_entreprise) VALUES
('Antananarivo', 'Lot II C 123, Antananarivo', '+261 34 12 345 67', 'contact@tanana-distribution.mg', 1),
('Toamasina', 'Rte Nationale 5, Toamasina', '+261 34 13 456 78', 'toamasina@madagro.mg', 2);

-- ------------------------------------------------------------------------------
-- 8. DEPOTS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO depot (nom, adresse, id_site, id_methode_valorisation_stock) VALUES
('Depot Antananarivo', 'Lot II C 123, Antananarivo', 1, 1),
('Depot Toamasina', 'Rte Nationale 5, Toamasina', 2, 2),
('Depot Antsirabe', 'Avenue de l''Industrie, Antsirabe', 1, 3);

-- ------------------------------------------------------------------------------
-- 9. FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO fournisseur (nom, adresse) VALUES
('Societe MADA Fourniture', 'Lot I A 45, Antananarivo'),
('Fournitures Tamatave SARL', 'Rte Toamasina, Toamasina'),
('Antsirabe Materiaux', 'Avenue de l''Industrie, Antsirabe');

-- ------------------------------------------------------------------------------
-- 10. CLIENTS (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO client (nom, adresse) VALUES
('Compagnie AGRI-MADA', 'Lot 23, Zone Agricole, Antananarivo'),
('Entreprise Locale S.A.R.L.', 'Avenue de l''Independance, Toamasina'),
('SARL Madagascar Commerce', 'Lot II M 45 Bis Analakely, Antananarivo'),
('Entreprise ROVA Distribution', '67 Avenue de l''Independance, Antananarivo'),
('Societe TIKO Trading', 'BP 1234 - Antsirabe 110'),
('SARL HERY Import Export', 'Rue Rainilaiarivony, Fianarantsoa');

-- ------------------------------------------------------------------------------
-- 11. ARTICLES (À INSeRER) - Articles électroniques (données réalistes)
-- ------------------------------------------------------------------------------
INSERT INTO article (reference, designation, prix_achat_ref, prix_vente_ref, id_unite, article_categorie_id) VALUES
('ART001', 'Samsung Galaxy A14 (128GB)', 420000.00, 560000.00, 6, 6),
('ART002', 'Dell Inspiron 14 5430 - Core i5, 8GB, 512GB SSD', 2800000.00, 3600000.00, 6, 6),
('ART003', 'TP-Link Archer AX20 (WiFi 6) - Routeur', 160000.00, 240000.00, 6, 6),
('ART004', 'Samsung Smart TV 43" Crystal UHD (AU7000)', 1400000.00, 1900000.00, 6, 6),
('ART005', 'HPE ProLiant MicroServer Gen10 Plus', 6500000.00, 9500000.00, 6, 6),
('ART006', 'Sony WH-CH720N - Casque Bluetooth', 60000.00, 95000.00, 6, 6),
('ART007', 'Anker PowerCore 20100mAh - Power Bank', 80000.00, 120000.00, 6, 6);

-- ------------------------------------------------------------------------------
-- 12. STOCK INITIAL (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO stock (article_id, depot_id, quantite_actuelle) VALUES
-- (1, 1, 500.00),
-- (2, 1, 200.00),
-- (3, 2, 1000.00),
-- (4, 2, 150.00),
-- (5, 3, 50.00),
-- (6, 1, 1000.00),
-- (7, 3, 5.00);

-- ------------------------------------------------------------------------------
-- 13. MOUVEMENTS DE STOCK INITIAUX (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO mouvement_stock (type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
-- ('ENTREE', 0.00, 500.00, 0.00, 500.00, 25.50, 1, 1),
-- ('ENTREE', 0.00, 200.00, 0.00, 200.00, 45.00, 2, 1),
-- ('ENTREE', 0.00, 1000.00, 0.00, 1000.00, 8.50, 3, 2),
-- ('ENTREE', 0.00, 150.00, 0.00, 150.00, 15.00, 4, 2),
-- ('ENTREE', 0.00, 50.00, 0.00, 50.00, 120.00, 5, 3),
-- ('ENTREE', 0.00, 1000.00, 0.00, 1000.00, 2.50, 6, 1),
-- ('ENTREE', 0.00, 5.00, 0.00, 5.00, 2500.00, 7, 3);

-- ------------------------------------------------------------------------------
-- 14. LOTS DE STOCK (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat) VALUES
-- ('LOT-001-2024', 1, 1, 1, 500.00, 500.00, 25.50),
-- ('LOT-002-2024', 2, 1, 2, 200.00, 200.00, 45.00),
-- ('LOT-003-2024', 3, 2, 3, 1000.00, 1000.00, 8.50),
-- ('LOT-004-2024', 4, 2, 4, 150.00, 150.00, 15.00),
-- ('LOT-005-2024', 5, 3, 5, 50.00, 50.00, 120.00),
-- ('LOT-006-2024', 6, 1, 6, 1000.00, 1000.00, 2.50),
-- ('LOT-007-2024', 7, 3, 7, 5.00, 5.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 15. DEMANDES D'ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
-- ('DA-2024-001', '2024-01-15', 1, 1, 1, '2024-02-15', 'Reapprovisionnement matieres premieres', 3),
-- ('DA-2024-002', '2024-01-20', 2, 2, 2, '2024-02-20', 'equipement production', 3);

-- ------------------------------------------------------------------------------
-- 16. DeTAILS DEMANDES D'ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
-- (1, 1, 300.00, 25.50),
-- (1, 2, 100.00, 45.00),
-- (2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 17. PROFORMAS FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat) VALUES
-- ('PF-2024-001', '2024-01-16', '2024-02-16', 1, 12750.00, 1),
-- ('PF-2024-002', '2024-01-21', '2024-02-21', 3, 5000.00, 2);

-- ------------------------------------------------------------------------------
-- 18. DeTAILS PROFORMAS FOURNISSEURS (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
-- (1, 1, 300.00, 25.50),
-- (1, 2, 100.00, 45.00),
-- (2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 19. BONS DE COMMANDE ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO bon_commande_achat (numero_bc, date_commande, id_proforma_fournisseur, id_status, montant_ttc) VALUES
-- ('BCA-2024-001', '2024-01-18', 1, 3, 12750.00),
-- ('BCA-2024-002', '2024-01-23', 2, 3, 5000.00);

-- ------------------------------------------------------------------------------
-- 20. DeTAILS BONS DE COMMANDE ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO bon_commande_achat_details (id_bon_commande_achat, id_article, quantite, prix_unitaire) VALUES
-- (1, 1, 300.00, 25.50),
-- (1, 2, 100.00, 45.00),
-- (2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 21. FACTURES ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO facture_achat (numero_facture_fournisseur, date_facture, id_bon_commande_achat, id_status, id_client, montant_ttc, reste_a_payer) VALUES
-- ('FA-2024-001', '2024-02-01', 1, 3, 1, 12750.00, 0.00),
-- ('FA-2024-002', '2024-02-05', 2, 3, 2, 5000.00, 0.00);

-- ------------------------------------------------------------------------------
-- 22. DeTAILS FACTURES ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO facture_achat_details (id_facture_achat, id_article, quantite, prix_unitaire) VALUES
-- (1, 1, 300.00, 25.50),
-- (1, 2, 100.00, 45.00),
-- (2, 7, 2.00, 2500.00);

-- ------------------------------------------------------------------------------
-- 23. PROFORMAS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO proforma_vente (numero_proforma, date_proforma, id_client, id_status, montant_ttc) VALUES
-- ('PV-2024-001', '2024-01-10', 1, 3, 4000.00),
-- ('PV-2024-002', '2024-01-12', 2, 3, 9000.00);

-- ------------------------------------------------------------------------------
-- 24. DeTAILS PROFORMAS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO proforma_vente_details (id_proforma_vente, id_article, quantite, prix_unitaire) VALUES
-- (1, 5, 20.00, 180.00),
-- (1, 6, 200.00, 4.00),
-- (2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 25. BONS DE COMMANDE VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO bon_commande_vente (numero_bc, date_commande, id_proforma_vente, id_client, id_status, montant_ttc) VALUES
-- ('BCV-2024-001', '2024-01-12', 1, 1, 3, 4000.00),
-- ('BCV-2024-002', '2024-01-14', 2, 2, 3, 9000.00);

-- ------------------------------------------------------------------------------
-- 26. DeTAILS BONS DE COMMANDE VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO bon_commande_vente_details (id_bon_commande_vente, id_article, quantite, prix_unitaire) VALUES
-- (1, 5, 20.00, 180.00),
-- (1, 6, 200.00, 4.00),
-- (2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 27. FACTURES VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO facture_vente (numero_facture, date_facture, id_bon_commande_vente, id_status, montant_ttc, reste_a_payer) VALUES
-- ('FV-2024-001', '2024-01-20', 1, 3, 4000.00, 0.00),
-- ('FV-2024-002', '2024-01-22', 2, 3, 9000.00, 0.00);

-- ------------------------------------------------------------------------------
-- 28. DeTAILS FACTURES VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO facture_vente_details (id_facture_vente, id_article, quantite, prix_unitaire) VALUES
-- (1, 5, 20.00, 180.00),
-- (1, 6, 200.00, 4.00),
-- (2, 5, 50.00, 180.00);

-- ------------------------------------------------------------------------------
-- 29. CAISSES (À INSeRER)
-- ------------------------------------------------------------------------------
INSERT INTO caisse (code_caisse, libelle, solde_actuel, entreprise_id) VALUES
('CAISSE_A', 'Caisse Entreprise A', 10000000000.00, 1),
('CAISSE_B', 'Caisse Entreprise B', 5000000000.00, 2);

-- ------------------------------------------------------------------------------
-- 30. MOUVEMENTS CAISSE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO caisse_mouvement (libelle_operation, montant_entree, montant_sortie, solde_avant, solde_apres, id_caisse) VALUES
-- ('Initialisation caisse', 10000.00, 0.00, 0.00, 10000.00, 1),
-- ('Initialisation caisse', 5000.00, 0.00, 0.00, 5000.00, 2),
-- ('Paiement achat', 0.00, 12750.00, 10000.00, -2750.00, 1),
-- ('Paiement achat', 0.00, 5000.00, 5000.00, 0.00, 2),
-- ('Paiement vente', 4000.00, 0.00, -2750.00, 1250.00, 1),
-- ('Paiement vente', 9000.00, 0.00, 0.00, 9000.00, 2);

-- ------------------------------------------------------------------------------
-- 31. PAIEMENTS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO paiement_vente (numero_recu, date_paiement, id_facture_vente, id_caisse_mouvement, montant_total_paye) VALUES
-- ('REC-2024-001', '2024-01-21', 1, 5, 4000.00),
-- ('REC-2024-002', '2024-01-23', 2, 6, 9000.00);

-- ------------------------------------------------------------------------------
-- 32. DeTAILS PAIEMENTS VENTE (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO paiement_vente_details (id_paiement_vente, montant) VALUES
-- (1, 4000.00),
-- (2, 9000.00);

-- ------------------------------------------------------------------------------
-- 33. PAIEMENTS ACHAT (À INSeRER)
-- ------------------------------------------------------------------------------
-- INSERT INTO paiement_achat (numero_paiement, date_paiement, id_facture_achat, id_caisse_mouvement, montant_total_paye) VALUES
-- ('PAI-2024-001', '2024-02-03', 1, 3, 12750.00),
-- ('PAI-2024-002', '2024-02-07', 2, 4, 5000.00);

-- -- ------------------------------------------------------------------------------
-- -- 34. DeTAILS PAIEMENTS ACHAT (À INSeRER)
-- -- ------------------------------------------------------------------------------
-- INSERT INTO paiement_achat_details (id_paiement_achat, montant, reference_externe) VALUES
-- (1, 12750.00, 'VIR-2024-001'),
-- (2, 5000.00, 'CHQ-2024-001');