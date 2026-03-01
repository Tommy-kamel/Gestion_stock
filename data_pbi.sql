-- =============================================================================
-- FICHIER : data_enriched_corrige.sql
-- DESCRIPTION : Données d'exemple enrichies pour la base gestion_stock
-- AVEC CORRECTION DES PROBLÈMES D'ENCODAGE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. UTILISATEUR
-- -----------------------------------------------------------------------------
INSERT INTO utilisateur (nom, mdp) VALUES
('admin', 'admin');

-- -----------------------------------------------------------------------------
-- 2. STATUS
-- -----------------------------------------------------------------------------
INSERT INTO status (code, libelle, niveau) VALUES
('BROUILLON', 'Brouillon', 1),
('EN_COURS', 'En cours', 2),
('VALIDE', 'Valide', 3),
('ANNULE', 'Annule', 4),
('CLOTURE', 'Cloture', 5);

-- -----------------------------------------------------------------------------
-- 3. UNITÉS DE MESURE
-- -----------------------------------------------------------------------------
INSERT INTO unite (code, libelle) VALUES
('KG', 'Kilogramme'),
('L', 'Litre'),
('M', 'Metre'),
('M2', 'Metre carre'),
('M3', 'Metre cube'),
('U', 'Unite');

-- -----------------------------------------------------------------------------
-- 4. CATÉGORIES D'ARTICLES (sans accents)
-- -----------------------------------------------------------------------------
INSERT INTO article_categorie (code, libelle) VALUES
('MAT_PREMIER', 'Matieres premieres'),
('PRODUIT_FINI', 'Produits finis'),
('CONSOMMABLE', 'Consommables'),
('EQUIPEMENT', 'Equipements'),
('SERVICE', 'Services'),
('ELECTRONIQUE', 'Articles electroniques');

-- -----------------------------------------------------------------------------
-- 5. MÉTHODES DE VALORISATION STOCK
-- -----------------------------------------------------------------------------
INSERT INTO methode_valorisation_stock (code, libelle, description) VALUES
('FIFO', 'First In First Out', 'Premier entre, premier sorti'),
('LIFO', 'Last In First Out', 'Dernier entre, premier sorti'),
('CMUP', 'Cout Moyen Unitaire Pondere', 'Moyenne ponderee des couts');

-- -----------------------------------------------------------------------------
-- 6. GROUPES D'ENTREPRISES
-- -----------------------------------------------------------------------------
INSERT INTO groupe (nom, description) VALUES
('Groupe Industriel ABC', 'Groupe specialise dans l''industrie manufacturiere');

-- -----------------------------------------------------------------------------
-- 7. ENTREPRISES
-- -----------------------------------------------------------------------------
INSERT INTO entreprise (nom, adresse, id_groupe) VALUES
('SARL Tananarive Distribution', 'Lot II C 123, Antananarivo', 1),
('Madagascar Agro Industrie', 'Zone Industrielle, Toamasina', 1);

-- -----------------------------------------------------------------------------
-- 8. SITES
-- -----------------------------------------------------------------------------
INSERT INTO site (nom, adresse, telephone, email, id_entreprise) VALUES
('Antananarivo', 'Lot II C 123, Antananarivo', '+261 34 12 345 67', 'contact@tanana-distribution.mg', 1),
('Toamasina', 'Rte Nationale 5, Toamasina', '+261 34 13 456 78', 'toamasina@madagro.mg', 2),
('Antsirabe', 'Avenue de l''Industrie, Antsirabe', '+261 34 14 567 89', 'antsirabe@tanana-distribution.mg', 1);

-- -----------------------------------------------------------------------------
-- 9. DÉPÔTS
-- -----------------------------------------------------------------------------
INSERT INTO depot (nom, adresse, id_site, id_methode_valorisation_stock) VALUES
('Depot Antananarivo (FIFO)', 'Lot II C 123, Antananarivo', 1, 1),
('Depot Toamasina (LIFO)', 'Rte Nationale 5, Toamasina', 2, 2),
('Depot Antsirabe (CMUP)', 'Avenue de l''Industrie, Antsirabe', 3, 3);

-- -----------------------------------------------------------------------------
-- 10. FOURNISSEURS
-- -----------------------------------------------------------------------------
INSERT INTO fournisseur (nom, adresse) VALUES
('Societe MADA Fourniture', 'Lot I A 45, Antananarivo'),
('Fournitures Tamatave SARL', 'Rte Toamasina, Toamasina'),
('Antsirabe Materiaux', 'Avenue de l''Industrie, Antsirabe'),
('Electro Import', 'Zone franche, Antananarivo'),
('Tech Distribution', 'Rue du Commerce, Antananarivo'),
('Global Supplies', 'Port de Toamasina'),
('Mada Pack', 'Lot III, Antsirabe');

-- -----------------------------------------------------------------------------
-- 11. CLIENTS
-- -----------------------------------------------------------------------------
INSERT INTO client (nom, adresse) VALUES
('Compagnie AGRI-MADA', 'Lot 23, Zone Agricole, Antananarivo'),
('Entreprise Locale S.A.R.L.', 'Avenue de l''Independance, Toamasina'),
('SARL Madagascar Commerce', 'Lot II M 45 Bis Analakely, Antananarivo'),
('Entreprise ROVA Distribution', '67 Avenue de l''Independance, Antananarivo'),
('Societe TIKO Trading', 'BP 1234 - Antsirabe 110'),
('SARL HERY Import Export', 'Rue Rainilaiarivony, Fianarantsoa'),
('Centre Commercial LEADER', 'Ankorondrano, Antananarivo'),
('Supermarché JUMBO', 'Toamasina'),
('Hotel ROYAL PALACE', 'Antsirabe'),
('Societe MINING', 'Moramanga');

-- -----------------------------------------------------------------------------
-- 12. ARTICLES (sans accents)
-- -----------------------------------------------------------------------------
INSERT INTO article (reference, designation, prix_achat_ref, prix_vente_ref, id_unite, article_categorie_id) VALUES
-- Electronique
('ART001', 'Samsung Galaxy A14 (128GB)', 420000.00, 560000.00, 6, 6),
('ART002', 'Dell Inspiron 14 5430 - Core i5, 8GB, 512GB SSD', 2800000.00, 3600000.00, 6, 6),
('ART003', 'TP-Link Archer AX20 (WiFi 6) - Routeur', 160000.00, 240000.00, 6, 6),
('ART004', 'Samsung Smart TV 43" Crystal UHD (AU7000)', 1400000.00, 1900000.00, 6, 6),
('ART005', 'HPE ProLiant MicroServer Gen10 Plus', 6500000.00, 9500000.00, 6, 6),
('ART006', 'Sony WH-CH720N - Casque Bluetooth', 60000.00, 95000.00, 6, 6),
('ART007', 'Anker PowerCore 20100mAh - Power Bank', 80000.00, 120000.00, 6, 6),
('ART008', 'Apple iPhone 14 128GB', 3500000.00, 4200000.00, 6, 6),
('ART009', 'Logitech MX Master 3S', 120000.00, 160000.00, 6, 6),
('ART010', 'Canon EOS 2000D', 1800000.00, 2300000.00, 6, 6),

-- Matieres premieres
('ART011', 'Farine de ble (sac 50kg)', 35000.00, 45000.00, 1, 1),
('ART012', 'Sucre granule (sac 50kg)', 40000.00, 52000.00, 1, 1),
('ART013', 'Huile de palme (bidon 20L)', 60000.00, 75000.00, 2, 1),
('ART014', 'Acier (barre 6m)', 120000.00, 150000.00, 3, 1),
('ART015', 'Bois (planche m³)', 250000.00, 320000.00, 5, 1),

-- Produits finis
('ART016', 'Pain de mie (500g)', 1500.00, 2500.00, 6, 2),
('ART017', 'Biscuits chocolates (200g)', 800.00, 1500.00, 6, 2),
('ART018', 'Jus d''orange (1L)', 2000.00, 3500.00, 2, 2),
('ART019', 'Savon liquide (500ml)', 2500.00, 4000.00, 2, 2),

-- Consommables
('ART020', 'Cartouche toner HP 85A', 45000.00, 65000.00, 6, 3),
('ART021', 'Papier A4 (ramette 500 feuilles)', 8000.00, 12000.00, 6, 3),
('ART022', 'Gants de protection (paire)', 2000.00, 3500.00, 6, 3),

-- Equipements
('ART023', 'Perceuse sans fil Bosch', 180000.00, 250000.00, 6, 4),
('ART024', 'Echelle telescopique 3m', 120000.00, 180000.00, 6, 4);

-- -----------------------------------------------------------------------------
-- 13. DEMANDES D'ACHAT
-- -----------------------------------------------------------------------------
-- Mois 1 (Janvier 2024)
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-001', '2024-01-05', 1, 1, 1, '2024-01-20', 'Reapprovisionnement stocks electronique', 3),
('DA-2024-002', '2024-01-10', 1, 1, 1, '2024-01-25', 'Achat matieres premieres boulangerie', 3),
('DA-2024-003', '2024-01-15', 2, 2, 2, '2024-01-30', 'Equipement atelier', 3);

-- Mois 2 (Fevrier 2024)
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-004', '2024-02-01', 1, 3, 3, '2024-02-15', 'Consommables bureau', 3),
('DA-2024-005', '2024-02-10', 1, 1, 1, '2024-02-25', 'Nouveaux smartphones', 3),
('DA-2024-006', '2024-02-20', 2, 2, 2, '2024-03-05', 'Matieres premieres agro', 2);

-- Mois 3 (Mars 2024)
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-007', '2024-03-05', 1, 1, 1, '2024-03-20', 'Achat TV pour client', 3),
('DA-2024-008', '2024-03-12', 1, 3, 3, '2024-03-28', 'Fournitures atelier', 3),
('DA-2024-009', '2024-03-18', 2, 2, 2, '2024-04-02', 'Equipement production', 1);

-- Mois 4 (Avril 2024)
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-010', '2024-04-02', 1, 1, 1, '2024-04-18', 'Reassort electromenager', 2),
('DA-2024-011', '2024-04-15', 1, 1, 1, '2024-04-30', 'Achat papier et cartouches', 3),
('DA-2024-012', '2024-04-25', 2, 2, 2, '2024-05-10', 'Matieres premieres', 3);

-- Mois 5 (Mai 2024)
INSERT INTO demande_achat (numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
('DA-2024-013', '2024-05-03', 1, 3, 3, '2024-05-20', 'Outillage', 3),
('DA-2024-014', '2024-05-14', 1, 1, 1, '2024-05-29', 'Smartphones', 2),
('DA-2024-015', '2024-05-22', 2, 2, 2, '2024-06-05', 'Produits alimentaires', 1);

-- -----------------------------------------------------------------------------
-- 14. DÉTAILS DEMANDES D'ACHAT
-- -----------------------------------------------------------------------------
-- DA-001 : articles electroniques
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(1, 1, 50, 420000.00),
(1, 2, 10, 2800000.00),
(1, 3, 30, 160000.00);

-- DA-002 : matieres premieres
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(2, 11, 100, 35000.00),
(2, 12, 80, 40000.00),
(2, 13, 40, 60000.00);

-- DA-003 : equipement
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(3, 23, 5, 180000.00),
(3, 24, 3, 120000.00);

-- DA-004 : consommables
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(4, 20, 20, 45000.00),
(4, 21, 100, 8000.00),
(4, 22, 50, 2000.00);

-- DA-005 : smartphones
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(5, 1, 30, 420000.00),
(5, 8, 15, 3500000.00);

-- DA-006 : matieres premieres agro
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(6, 11, 200, 35000.00),
(6, 12, 150, 40000.00),
(6, 13, 80, 60000.00);

-- DA-007 : TV
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(7, 4, 10, 1400000.00);

-- DA-008 : fournitures atelier
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(8, 14, 50, 120000.00),
(8, 15, 20, 250000.00);

-- DA-009 : equipement production
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(9, 5, 2, 6500000.00);

-- DA-010 : electromenager
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(10, 4, 5, 1400000.00),
(10, 6, 50, 60000.00);

-- DA-011 : papeterie
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(11, 20, 30, 45000.00),
(11, 21, 200, 8000.00);

-- DA-012 : matieres premieres
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(12, 11, 300, 35000.00),
(12, 13, 100, 60000.00);

-- DA-013 : outillage
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(13, 23, 8, 180000.00),
(13, 24, 5, 120000.00);

-- DA-014 : smartphones
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(14, 1, 20, 420000.00),
(14, 8, 10, 3500000.00);

-- DA-015 : produits alimentaires
INSERT INTO demande_achat_details (id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(15, 16, 500, 1500.00),
(15, 17, 1000, 800.00),
(15, 18, 300, 2000.00);

-- -----------------------------------------------------------------------------
-- 15. PROFORMAS FOURNISSEURS
-- -----------------------------------------------------------------------------
-- Proforma pour DA-001 (fournisseur 1)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-001', '2024-01-06', '2024-02-06', 1, (50*420000 + 10*2800000 + 30*160000), 1, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(1, 1, 50, 420000.00),
(1, 2, 10, 2800000.00),
(1, 3, 30, 160000.00);

-- Proforma pour DA-002 (fournisseur 2)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-002', '2024-01-11', '2024-02-11', 2, (100*35000 + 80*40000 + 40*60000), 2, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(2, 11, 100, 35000.00),
(2, 12, 80, 40000.00),
(2, 13, 40, 60000.00);

-- Proforma pour DA-003 (fournisseur 3)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-003', '2024-01-16', '2024-02-16', 3, (5*180000 + 3*120000), 3, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(3, 23, 5, 180000.00),
(3, 24, 3, 120000.00);

-- Proforma pour DA-004 (fournisseur 4)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-004', '2024-02-02', '2024-03-02', 4, (20*45000 + 100*8000 + 50*2000), 4, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(4, 20, 20, 45000.00),
(4, 21, 100, 8000.00),
(4, 22, 50, 2000.00);

-- Proforma pour DA-005 (fournisseur 5)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-005', '2024-02-11', '2024-03-11', 5, (30*420000 + 15*3500000), 5, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(5, 1, 30, 420000.00),
(5, 8, 15, 3500000.00);

-- Proforma pour DA-006 (fournisseur 6)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-006', '2024-02-21', '2024-03-21', 6, (200*35000 + 150*40000 + 80*60000), 6, 2);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(6, 11, 200, 35000.00),
(6, 12, 150, 40000.00),
(6, 13, 80, 60000.00);

-- Proforma pour DA-007 (fournisseur 7)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-007', '2024-03-06', '2024-04-06', 7, (10*1400000), 7, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(7, 4, 10, 1400000.00);

-- Proforma pour DA-008 (fournisseur 1)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-008', '2024-03-13', '2024-04-13', 1, (50*120000 + 20*250000), 8, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(8, 14, 50, 120000.00),
(8, 15, 20, 250000.00);

-- Proforma pour DA-009 (fournisseur 2)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-009', '2024-03-19', '2024-04-19', 2, (2*6500000), 9, 1);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(9, 5, 2, 6500000.00);

-- Proforma pour DA-010 (fournisseur 3)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-010', '2024-04-03', '2024-05-03', 3, (5*1400000 + 50*60000), 10, 2);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(10, 4, 5, 1400000.00),
(10, 6, 50, 60000.00);

-- Proforma pour DA-011 (fournisseur 4)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-011', '2024-04-16', '2024-05-16', 4, (30*45000 + 200*8000), 11, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(11, 20, 30, 45000.00),
(11, 21, 200, 8000.00);

-- Proforma pour DA-012 (fournisseur 5)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-012', '2024-04-26', '2024-05-26', 5, (300*35000 + 100*60000), 12, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(12, 11, 300, 35000.00),
(12, 13, 100, 60000.00);

-- Proforma pour DA-013 (fournisseur 6)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-013', '2024-05-04', '2024-06-04', 6, (8*180000 + 5*120000), 13, 3);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(13, 23, 8, 180000.00),
(13, 24, 5, 120000.00);

-- Proforma pour DA-014 (fournisseur 7)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-014', '2024-05-15', '2024-06-15', 7, (20*420000 + 10*3500000), 14, 2);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(14, 1, 20, 420000.00),
(14, 8, 10, 3500000.00);

-- Proforma pour DA-015 (fournisseur 1)
INSERT INTO proforma_fournisseur (numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
('PF-2024-015', '2024-05-23', '2024-06-23', 1, (500*1500 + 1000*800 + 300*2000), 15, 1);

INSERT INTO proforma_fournisseur_details (id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(15, 16, 500, 1500.00),
(15, 17, 1000, 800.00),
(15, 18, 300, 2000.00);

-- -----------------------------------------------------------------------------
-- 16. BONS DE COMMANDE ACHAT
-- -----------------------------------------------------------------------------
INSERT INTO bon_commande_achat (numero_bc, date_commande, id_proforma_fournisseur, id_status, montant_ttc) VALUES
('BCA-2024-001', '2024-01-08', 1, 3, (50*420000 + 10*2800000 + 30*160000)),
('BCA-2024-002', '2024-01-13', 2, 3, (100*35000 + 80*40000 + 40*60000)),
('BCA-2024-003', '2024-01-18', 3, 3, (5*180000 + 3*120000)),
('BCA-2024-004', '2024-02-04', 4, 3, (20*45000 + 100*8000 + 50*2000)),
('BCA-2024-005', '2024-02-13', 5, 3, (30*420000 + 15*3500000)),
('BCA-2024-006', '2024-03-08', 7, 3, (10*1400000)),
('BCA-2024-007', '2024-03-15', 8, 3, (50*120000 + 20*250000)),
('BCA-2024-008', '2024-04-18', 11, 3, (30*45000 + 200*8000)),
('BCA-2024-009', '2024-04-28', 12, 3, (300*35000 + 100*60000)),
('BCA-2024-010', '2024-05-06', 13, 3, (8*180000 + 5*120000));

-- Détails des BC
INSERT INTO bon_commande_achat_details (id_bon_commande_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 50, 420000.00),
(1, 2, 10, 2800000.00),
(1, 3, 30, 160000.00),
(2, 11, 100, 35000.00),
(2, 12, 80, 40000.00),
(2, 13, 40, 60000.00),
(3, 23, 5, 180000.00),
(3, 24, 3, 120000.00),
(4, 20, 20, 45000.00),
(4, 21, 100, 8000.00),
(4, 22, 50, 2000.00),
(5, 1, 30, 420000.00),
(5, 8, 15, 3500000.00),
(6, 4, 10, 1400000.00),
(7, 14, 50, 120000.00),
(7, 15, 20, 250000.00),
(8, 20, 30, 45000.00),
(8, 21, 200, 8000.00),
(9, 11, 300, 35000.00),
(9, 13, 100, 60000.00),
(10, 23, 8, 180000.00),
(10, 24, 5, 120000.00);

-- -----------------------------------------------------------------------------
-- 17. FACTURES ACHAT
-- -----------------------------------------------------------------------------
INSERT INTO facture_achat (numero_facture_fournisseur, date_facture, id_bon_commande_achat, id_status, montant_ttc) VALUES
('FA-2024-001', '2024-01-20', 1, 3, (50*420000 + 10*2800000 + 30*160000)),
('FA-2024-002', '2024-01-25', 2, 3, (100*35000 + 80*40000 + 40*60000)),
('FA-2024-003', '2024-01-30', 3, 3, (5*180000 + 3*120000)),
('FA-2024-004', '2024-02-10', 4, 3, (20*45000 + 100*8000 + 50*2000)),
('FA-2024-005', '2024-02-20', 5, 3, (30*420000 + 15*3500000)),
('FA-2024-006', '2024-03-15', 6, 3, (10*1400000)),
('FA-2024-007', '2024-03-25', 7, 3, (50*120000 + 20*250000)),
('FA-2024-008', '2024-04-25', 8, 3, (30*45000 + 200*8000)),
('FA-2024-009', '2024-05-05', 9, 3, (300*35000 + 100*60000)),
('FA-2024-010', '2024-05-15', 10, 3, (8*180000 + 5*120000));

-- Détails factures achat
INSERT INTO facture_achat_details (id_facture_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 50, 420000.00),
(1, 2, 10, 2800000.00),
(1, 3, 30, 160000.00),
(2, 11, 100, 35000.00),
(2, 12, 80, 40000.00),
(2, 13, 40, 60000.00),
(3, 23, 5, 180000.00),
(3, 24, 3, 120000.00),
(4, 20, 20, 45000.00),
(4, 21, 100, 8000.00),
(4, 22, 50, 2000.00),
(5, 1, 30, 420000.00),
(5, 8, 15, 3500000.00),
(6, 4, 10, 1400000.00),
(7, 14, 50, 120000.00),
(7, 15, 20, 250000.00),
(8, 20, 30, 45000.00),
(8, 21, 200, 8000.00),
(9, 11, 300, 35000.00),
(9, 13, 100, 60000.00),
(10, 23, 8, 180000.00),
(10, 24, 5, 120000.00);

-- -----------------------------------------------------------------------------
-- 18. MOUVEMENTS DE STOCK (entrees)
-- -----------------------------------------------------------------------------
INSERT INTO mouvement_stock (date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
('2024-01-20', 'ENTREE', 0, 50, 0, 50, 420000, 1, 1),
('2024-01-20', 'ENTREE', 0, 10, 0, 10, 2800000, 2, 1),
('2024-01-20', 'ENTREE', 0, 30, 0, 30, 160000, 3, 1),
('2024-01-25', 'ENTREE', 0, 100, 0, 100, 35000, 11, 1),
('2024-01-25', 'ENTREE', 0, 80, 0, 80, 40000, 12, 1),
('2024-01-25', 'ENTREE', 0, 40, 0, 40, 60000, 13, 1),
('2024-01-30', 'ENTREE', 0, 5, 0, 5, 180000, 23, 2),
('2024-01-30', 'ENTREE', 0, 3, 0, 3, 120000, 24, 2),
('2024-02-10', 'ENTREE', 0, 20, 0, 20, 45000, 20, 3),
('2024-02-10', 'ENTREE', 0, 100, 0, 100, 8000, 21, 3),
('2024-02-10', 'ENTREE', 0, 50, 0, 50, 2000, 22, 3),
('2024-02-20', 'ENTREE', 50, 30, 0, 80, 420000, 1, 1),
('2024-02-20', 'ENTREE', 0, 15, 0, 15, 3500000, 8, 1),
('2024-03-15', 'ENTREE', 0, 10, 0, 10, 1400000, 4, 1),
('2024-03-25', 'ENTREE', 0, 50, 0, 50, 120000, 14, 3),
('2024-03-25', 'ENTREE', 0, 20, 0, 20, 250000, 15, 3),
('2024-04-25', 'ENTREE', 20, 30, 0, 50, 45000, 20, 3),
('2024-04-25', 'ENTREE', 100, 200, 0, 300, 8000, 21, 3),
('2024-05-05', 'ENTREE', 100, 300, 0, 400, 35000, 11, 1),
('2024-05-05', 'ENTREE', 40, 100, 0, 140, 60000, 13, 1),
('2024-05-15', 'ENTREE', 5, 8, 0, 13, 180000, 23, 2),
('2024-05-15', 'ENTREE', 3, 5, 0, 8, 120000, 24, 2);

-- -----------------------------------------------------------------------------
-- 19. LOTS DE STOCK
-- -----------------------------------------------------------------------------
INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-001-2024', 1, 1, id, 50, 50, 420000
FROM mouvement_stock WHERE date_mouvement = '2024-01-20' AND id_article = 1 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-002-2024', 2, 1, id, 10, 10, 2800000
FROM mouvement_stock WHERE date_mouvement = '2024-01-20' AND id_article = 2 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-003-2024', 3, 1, id, 30, 30, 160000
FROM mouvement_stock WHERE date_mouvement = '2024-01-20' AND id_article = 3 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-004-2024', 11, 1, id, 100, 100, 35000
FROM mouvement_stock WHERE date_mouvement = '2024-01-25' AND id_article = 11 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-005-2024', 12, 1, id, 80, 80, 40000
FROM mouvement_stock WHERE date_mouvement = '2024-01-25' AND id_article = 12 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-006-2024', 13, 1, id, 40, 40, 60000
FROM mouvement_stock WHERE date_mouvement = '2024-01-25' AND id_article = 13 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-007-2024', 23, 2, id, 5, 5, 180000
FROM mouvement_stock WHERE date_mouvement = '2024-01-30' AND id_article = 23 AND id_depot = 2;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-008-2024', 24, 2, id, 3, 3, 120000
FROM mouvement_stock WHERE date_mouvement = '2024-01-30' AND id_article = 24 AND id_depot = 2;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-009-2024', 20, 3, id, 20, 20, 45000
FROM mouvement_stock WHERE date_mouvement = '2024-02-10' AND id_article = 20 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-010-2024', 21, 3, id, 100, 100, 8000
FROM mouvement_stock WHERE date_mouvement = '2024-02-10' AND id_article = 21 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-011-2024', 22, 3, id, 50, 50, 2000
FROM mouvement_stock WHERE date_mouvement = '2024-02-10' AND id_article = 22 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-012-2024', 1, 1, id, 30, 30, 420000
FROM mouvement_stock WHERE date_mouvement = '2024-02-20' AND id_article = 1 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-013-2024', 8, 1, id, 15, 15, 3500000
FROM mouvement_stock WHERE date_mouvement = '2024-02-20' AND id_article = 8 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-014-2024', 4, 1, id, 10, 10, 1400000
FROM mouvement_stock WHERE date_mouvement = '2024-03-15' AND id_article = 4 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-015-2024', 14, 3, id, 50, 50, 120000
FROM mouvement_stock WHERE date_mouvement = '2024-03-25' AND id_article = 14 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-016-2024', 15, 3, id, 20, 20, 250000
FROM mouvement_stock WHERE date_mouvement = '2024-03-25' AND id_article = 15 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-017-2024', 20, 3, id, 30, 30, 45000
FROM mouvement_stock WHERE date_mouvement = '2024-04-25' AND id_article = 20 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-018-2024', 21, 3, id, 200, 200, 8000
FROM mouvement_stock WHERE date_mouvement = '2024-04-25' AND id_article = 21 AND id_depot = 3;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-019-2024', 11, 1, id, 300, 300, 35000
FROM mouvement_stock WHERE date_mouvement = '2024-05-05' AND id_article = 11 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-020-2024', 13, 1, id, 100, 100, 60000
FROM mouvement_stock WHERE date_mouvement = '2024-05-05' AND id_article = 13 AND id_depot = 1;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-021-2024', 23, 2, id, 8, 8, 180000
FROM mouvement_stock WHERE date_mouvement = '2024-05-15' AND id_article = 23 AND id_depot = 2;

INSERT INTO lot_stock (numero_lot, id_article, id_depot, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat)
SELECT 'LOT-022-2024', 24, 2, id, 5, 5, 120000
FROM mouvement_stock WHERE date_mouvement = '2024-05-15' AND id_article = 24 AND id_depot = 2;

-- -----------------------------------------------------------------------------
-- 20. TABLE STOCK
-- -----------------------------------------------------------------------------
INSERT INTO stock (article_id, depot_id, quantite_actuelle, date_maj) VALUES
(1, 1, 80, NOW()),
(2, 1, 10, NOW()),
(3, 1, 30, NOW()),
(4, 1, 10, NOW()),
(8, 1, 15, NOW()),
(11, 1, 400, NOW()),
(12, 1, 80, NOW()),
(13, 1, 140, NOW()),
(20, 3, 50, NOW()),
(21, 3, 300, NOW()),
(22, 3, 50, NOW()),
(23, 2, 13, NOW()),
(24, 2, 8, NOW()),
(14, 3, 50, NOW()),
(15, 3, 20, NOW());

-- -----------------------------------------------------------------------------
-- 21. DEMANDES D'ACHAT CLIENT
-- -----------------------------------------------------------------------------
INSERT INTO demande_achat_client (numero_da, date_demande, id_client, date_souhaitee, id_status) VALUES
('DAC-2024-001', '2024-01-05', 1, '2024-01-20', 3),
('DAC-2024-002', '2024-01-12', 2, '2024-01-25', 3),
('DAC-2024-003', '2024-01-18', 3, '2024-02-01', 3),
('DAC-2024-004', '2024-02-02', 4, '2024-02-15', 3),
('DAC-2024-005', '2024-02-10', 5, '2024-02-25', 2),
('DAC-2024-006', '2024-02-20', 6, '2024-03-05', 3),
('DAC-2024-007', '2024-03-01', 7, '2024-03-15', 3),
('DAC-2024-008', '2024-03-10', 8, '2024-03-25', 3),
('DAC-2024-009', '2024-03-20', 9, '2024-04-05', 1),
('DAC-2024-010', '2024-04-05', 10, '2024-04-20', 3),
('DAC-2024-011', '2024-04-15', 1, '2024-04-30', 3),
('DAC-2024-012', '2024-04-25', 2, '2024-05-10', 3),
('DAC-2024-013', '2024-05-05', 3, '2024-05-20', 2),
('DAC-2024-014', '2024-05-15', 4, '2024-05-30', 1),
('DAC-2024-015', '2024-05-25', 5, '2024-06-10', 3);

-- Détails des demandes clients
INSERT INTO demande_achat_client_details (id_demande_achat_client, id_article, quantite_demandee, prix_unitaire) VALUES
(1, 1, 10, 560000.00),
(1, 2, 2, 3600000.00),
(1, 3, 5, 240000.00),
(2, 16, 100, 2500.00),
(2, 17, 200, 1500.00),
(2, 18, 50, 3500.00),
(3, 23, 2, 250000.00),
(3, 24, 1, 180000.00),
(4, 11, 50, 45000.00),
(4, 12, 30, 52000.00),
(5, 4, 3, 1900000.00),
(5, 6, 20, 95000.00),
(6, 20, 10, 65000.00),
(6, 21, 50, 12000.00),
(7, 1, 15, 560000.00),
(7, 8, 5, 4200000.00),
(8, 16, 200, 2500.00),
(8, 17, 300, 1500.00),
(9, 23, 3, 250000.00),
(9, 24, 2, 180000.00),
(10, 4, 2, 1900000.00),
(10, 6, 30, 95000.00),
(11, 11, 100, 45000.00),
(11, 13, 40, 75000.00),
(12, 20, 15, 65000.00),
(12, 21, 100, 12000.00),
(13, 18, 80, 3500.00),
(13, 19, 60, 4000.00),
(14, 23, 4, 250000.00),
(14, 24, 3, 180000.00),
(15, 1, 20, 560000.00),
(15, 2, 3, 3600000.00);

-- -----------------------------------------------------------------------------
-- 22. PROFORMAS VENTE
-- -----------------------------------------------------------------------------
INSERT INTO proforma_vente (numero_proforma, date_proforma, id_status, montant_ttc, id_demande_achat_client) VALUES
('PV-2024-001', '2024-01-06', 3, (10*560000 + 2*3600000 + 5*240000), 1),
('PV-2024-002', '2024-01-13', 3, (100*2500 + 200*1500 + 50*3500), 2),
('PV-2024-003', '2024-01-19', 3, (2*250000 + 1*180000), 3),
('PV-2024-004', '2024-02-03', 3, (50*45000 + 30*52000), 4),
('PV-2024-005', '2024-02-11', 2, (3*1900000 + 20*95000), 5),
('PV-2024-006', '2024-02-21', 3, (10*65000 + 50*12000), 6),
('PV-2024-007', '2024-03-02', 3, (15*560000 + 5*4200000), 7),
('PV-2024-008', '2024-03-11', 3, (200*2500 + 300*1500), 8),
('PV-2024-009', '2024-03-21', 1, (3*250000 + 2*180000), 9),
('PV-2024-010', '2024-04-06', 3, (2*1900000 + 30*95000), 10),
('PV-2024-011', '2024-04-16', 3, (100*45000 + 40*75000), 11),
('PV-2024-012', '2024-04-26', 3, (15*65000 + 100*12000), 12),
('PV-2024-013', '2024-05-06', 2, (80*3500 + 60*4000), 13),
('PV-2024-014', '2024-05-16', 1, (4*250000 + 3*180000), 14),
('PV-2024-015', '2024-05-26', 3, (20*560000 + 3*3600000), 15);

-- Détails des proformas vente
INSERT INTO proforma_vente_details (id_proforma_vente, id_article, quantite, prix_unitaire) VALUES
(1, 1, 10, 560000.00),
(1, 2, 2, 3600000.00),
(1, 3, 5, 240000.00),
(2, 16, 100, 2500.00),
(2, 17, 200, 1500.00),
(2, 18, 50, 3500.00),
(3, 23, 2, 250000.00),
(3, 24, 1, 180000.00),
(4, 11, 50, 45000.00),
(4, 12, 30, 52000.00),
(5, 4, 3, 1900000.00),
(5, 6, 20, 95000.00),
(6, 20, 10, 65000.00),
(6, 21, 50, 12000.00),
(7, 1, 15, 560000.00),
(7, 8, 5, 4200000.00),
(8, 16, 200, 2500.00),
(8, 17, 300, 1500.00),
(9, 23, 3, 250000.00),
(9, 24, 2, 180000.00),
(10, 4, 2, 1900000.00),
(10, 6, 30, 95000.00),
(11, 11, 100, 45000.00),
(11, 13, 40, 75000.00),
(12, 20, 15, 65000.00),
(12, 21, 100, 12000.00),
(13, 18, 80, 3500.00),
(13, 19, 60, 4000.00),
(14, 23, 4, 250000.00),
(14, 24, 3, 180000.00),
(15, 1, 20, 560000.00),
(15, 2, 3, 3600000.00);

-- -----------------------------------------------------------------------------
-- 23. BONS DE COMMANDE VENTE
-- -----------------------------------------------------------------------------
INSERT INTO bon_commande_vente (numero_bc, date_commande, id_proforma_vente, id_client, id_status, montant_ttc) VALUES
('BCV-2024-001', '2024-01-08', 1, 1, 3, (10*560000 + 2*3600000 + 5*240000)),
('BCV-2024-002', '2024-01-15', 2, 2, 3, (100*2500 + 200*1500 + 50*3500)),
('BCV-2024-003', '2024-01-21', 3, 3, 3, (2*250000 + 1*180000)),
('BCV-2024-004', '2024-02-05', 4, 4, 3, (50*45000 + 30*52000)),
('BCV-2024-005', '2024-02-23', 6, 6, 3, (10*65000 + 50*12000)),
('BCV-2024-006', '2024-03-04', 7, 7, 3, (15*560000 + 5*4200000)),
('BCV-2024-007', '2024-03-13', 8, 8, 3, (200*2500 + 300*1500)),
('BCV-2024-008', '2024-04-08', 10, 10, 3, (2*1900000 + 30*95000)),
('BCV-2024-009', '2024-04-18', 11, 1, 3, (100*45000 + 40*75000)),
('BCV-2024-010', '2024-04-28', 12, 2, 3, (15*65000 + 100*12000)),
('BCV-2024-011', '2024-05-28', 15, 5, 3, (20*560000 + 3*3600000));

-- Détails des BC vente
INSERT INTO bon_commande_vente_details (id_bon_commande_vente, id_article, quantite, prix_unitaire) VALUES
(1, 1, 10, 560000.00),
(1, 2, 2, 3600000.00),
(1, 3, 5, 240000.00),
(2, 16, 100, 2500.00),
(2, 17, 200, 1500.00),
(2, 18, 50, 3500.00),
(3, 23, 2, 250000.00),
(3, 24, 1, 180000.00),
(4, 11, 50, 45000.00),
(4, 12, 30, 52000.00),
(5, 20, 10, 65000.00),
(5, 21, 50, 12000.00),
(6, 1, 15, 560000.00),
(6, 8, 5, 4200000.00),
(7, 16, 200, 2500.00),
(7, 17, 300, 1500.00),
(8, 4, 2, 1900000.00),
(8, 6, 30, 95000.00),
(9, 11, 100, 45000.00),
(9, 13, 40, 75000.00),
(10, 20, 15, 65000.00),
(10, 21, 100, 12000.00),
(11, 1, 20, 560000.00),
(11, 2, 3, 3600000.00);

-- -----------------------------------------------------------------------------
-- 24. FACTURES VENTE
-- -----------------------------------------------------------------------------
INSERT INTO facture_vente (numero_facture, date_facture, id_bon_commande_vente, id_status, montant_ttc, reste_a_payer) VALUES
('FV-2024-001', '2024-01-20', 1, 3, (10*560000 + 2*3600000 + 5*240000), 0),
('FV-2024-002', '2024-01-28', 2, 3, (100*2500 + 200*1500 + 50*3500), 0),
('FV-2024-003', '2024-02-01', 3, 3, (2*250000 + 1*180000), 0),
('FV-2024-004', '2024-02-15', 4, 3, (50*45000 + 30*52000), 200000),
('FV-2024-005', '2024-03-01', 5, 3, (10*65000 + 50*12000), 0),
('FV-2024-006', '2024-03-10', 6, 3, (15*560000 + 5*4200000), 500000),
('FV-2024-007', '2024-03-20', 7, 3, (200*2500 + 300*1500), 0),
('FV-2024-008', '2024-04-15', 8, 3, (2*1900000 + 30*95000), 0),
('FV-2024-009', '2024-04-25', 9, 3, (100*45000 + 40*75000), 0),
('FV-2024-010', '2024-05-05', 10, 3, (15*65000 + 100*12000), 0),
('FV-2024-011', '2024-06-01', 11, 3, (20*560000 + 3*3600000), 0);

-- Détails factures vente
INSERT INTO facture_vente_details (id_facture_vente, id_article, quantite, prix_unitaire) VALUES
(1, 1, 10, 560000.00),
(1, 2, 2, 3600000.00),
(1, 3, 5, 240000.00),
(2, 16, 100, 2500.00),
(2, 17, 200, 1500.00),
(2, 18, 50, 3500.00),
(3, 23, 2, 250000.00),
(3, 24, 1, 180000.00),
(4, 11, 50, 45000.00),
(4, 12, 30, 52000.00),
(5, 20, 10, 65000.00),
(5, 21, 50, 12000.00),
(6, 1, 15, 560000.00),
(6, 8, 5, 4200000.00),
(7, 16, 200, 2500.00),
(7, 17, 300, 1500.00),
(8, 4, 2, 1900000.00),
(8, 6, 30, 95000.00),
(9, 11, 100, 45000.00),
(9, 13, 40, 75000.00),
(10, 20, 15, 65000.00),
(10, 21, 100, 12000.00),
(11, 1, 20, 560000.00),
(11, 2, 3, 3600000.00);

-- -----------------------------------------------------------------------------
-- 25. MOUVEMENTS DE STOCK (sorties)
-- -----------------------------------------------------------------------------
INSERT INTO mouvement_stock (date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
('2024-01-20', 'SORTIE', 80, 0, 10, 70, 560000, 1, 1),
('2024-01-20', 'SORTIE', 10, 0, 2, 8, 3600000, 2, 1),
('2024-01-20', 'SORTIE', 30, 0, 5, 25, 240000, 3, 1),
('2024-02-01', 'SORTIE', 13, 0, 2, 11, 250000, 23, 2),
('2024-02-01', 'SORTIE', 8, 0, 1, 7, 180000, 24, 2),
('2024-02-15', 'SORTIE', 400, 0, 50, 350, 45000, 11, 1),
('2024-02-15', 'SORTIE', 80, 0, 30, 50, 52000, 12, 1),
('2024-03-01', 'SORTIE', 50, 0, 10, 40, 65000, 20, 3),
('2024-03-01', 'SORTIE', 300, 0, 50, 250, 12000, 21, 3),
('2024-03-10', 'SORTIE', 70, 0, 15, 55, 560000, 1, 1),
('2024-03-10', 'SORTIE', 15, 0, 5, 10, 4200000, 8, 1),
('2024-04-15', 'SORTIE', 10, 0, 2, 8, 1900000, 4, 1),
('2024-04-25', 'SORTIE', 350, 0, 100, 250, 45000, 11, 1),
('2024-04-25', 'SORTIE', 140, 0, 40, 100, 75000, 13, 1),
('2024-05-05', 'SORTIE', 40, 0, 15, 25, 65000, 20, 3),
('2024-05-05', 'SORTIE', 250, 0, 100, 150, 12000, 21, 3),
('2024-06-01', 'SORTIE', 55, 0, 20, 35, 560000, 1, 1),
('2024-06-01', 'SORTIE', 8, 0, 3, 5, 3600000, 2, 1);

-- -----------------------------------------------------------------------------
-- 26. CAISSES
-- -----------------------------------------------------------------------------
INSERT INTO caisse (code_caisse, libelle, solde_actuel, entreprise_id) VALUES
('CAISSE_A', 'Caisse Entreprise A', 10000000000.00, 1),
('CAISSE_B', 'Caisse Entreprise B', 5000000000.00, 2);

-- -----------------------------------------------------------------------------
-- 27. MOUVEMENTS CAISSE
-- -----------------------------------------------------------------------------
INSERT INTO caisse_mouvement (date_mouvement, libelle_operation, montant_entree, montant_sortie, solde_avant, solde_apres, id_caisse) VALUES
('2024-01-20', 'Paiement facture vente FV-2024-001', 10000000, 0, 10000000, 20000000, 1),
('2024-01-21', 'Paiement fournisseur', 0, 5000000, 20000000, 15000000, 1),
('2024-02-01', 'Encaissement client', 8000000, 0, 15000000, 23000000, 1),
('2024-02-10', 'Achat materiel', 0, 3000000, 23000000, 20000000, 1),
('2024-03-05', 'Paiement facture', 12000000, 0, 20000000, 32000000, 1),
('2024-03-15', 'Reglement fournisseur', 0, 7000000, 32000000, 25000000, 1),
('2024-04-01', 'Vente au comptant', 5000000, 0, 25000000, 30000000, 1),
('2024-04-20', 'Achat stocks', 0, 4000000, 30000000, 26000000, 1),
('2024-05-05', 'Encaissement', 9000000, 0, 26000000, 35000000, 1),
('2024-05-25', 'Paiement loyer', 0, 2000000, 35000000, 33000000, 1);

INSERT INTO caisse_mouvement (date_mouvement, libelle_operation, montant_entree, montant_sortie, solde_avant, solde_apres, id_caisse) VALUES
('2024-01-15', 'Depot initial', 5000000, 0, 5000000, 10000000, 2),
('2024-02-10', 'Paiement facture', 0, 3000000, 10000000, 7000000, 2),
('2024-03-01', 'Vente', 6000000, 0, 7000000, 13000000, 2),
('2024-04-05', 'Achat', 0, 2500000, 13000000, 10500000, 2),
('2024-05-12', 'Encaissement', 4000000, 0, 10500000, 14500000, 2);

-- -----------------------------------------------------------------------------
-- 28. PAIEMENTS VENTE
-- -----------------------------------------------------------------------------
INSERT INTO paiement_vente (numero_recu, date_paiement, id_facture_vente, id_caisse_mouvement, montant_total_paye)
SELECT 'REC-2024-001', '2024-01-20', 1, id, 10000000
FROM caisse_mouvement WHERE libelle_operation = 'Paiement facture vente FV-2024-001';

INSERT INTO paiement_vente_details (id_paiement_vente, montant) VALUES (1, 10000000);

-- Fin du script