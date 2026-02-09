-- data2.sql
-- Script d'insertion de données de test pour gestion_stock
-- Contient : statuts, unités, catégories, méthodes, entreprises, sites, dépôts,
-- fournisseurs, clients, articles, stocks, mouvements de stock, lots, et quelques achats/ventes.

BEGIN;

-- STATUTS
INSERT INTO status(id, code, libelle, niveau) VALUES
(1, 'NOUVEAU', 'Nouveau', 0),
(2, 'EN_COURS', 'En cours', 1),
(3, 'VALIDE', 'Validé', 2),
(4, 'ANNULE', 'Annulé', 0)
ON CONFLICT DO NOTHING;

-- UNITÉS
INSERT INTO unite(id, code, libelle) VALUES
(1, 'PCS', 'Pièce(s)'),
(2, 'KG', 'Kilogramme'),
(3, 'L', 'Litre'),
(4, 'M', 'Mètre')
ON CONFLICT DO NOTHING;

-- CATEGORIES D'ARTICLES
INSERT INTO article_categorie(id, code, libelle) VALUES
(1, 'MAT', 'Matériel'),
(2, 'CONSO', 'Consommable'),
(3, 'EMB', 'Emballage')
ON CONFLICT DO NOTHING;

-- METHODES DE VALORISATION
INSERT INTO methode_valorisation_stock(id, code, libelle, description) VALUES
(1, 'FIFO', 'FIFO', 'First In First Out'),
(2, 'LIFO', 'LIFO', 'Last In First Out'),
(3, 'CMUP', 'CMUP', 'Coût moyen unitaire pondéré')
ON CONFLICT DO NOTHING;

-- GROUPES / ENTREPRISES / SITES / DEPOTS
INSERT INTO groupe(id, nom, description) VALUES
(1, 'Groupe Malagasy', 'Groupe de test - Madagascar')
ON CONFLICT DO NOTHING;

INSERT INTO entreprise(id, nom, adresse, id_groupe) VALUES
(1, 'Entreprise Tovo', 'Lot II A Ankadifotsy, Antananarivo', 1)
ON CONFLICT DO NOTHING;

INSERT INTO site(id, nom, adresse, telephone, email, id_entreprise) VALUES
(1, 'Siège Antananarivo', 'Ankadifotsy, Antananarivo', '0341234567', 'contact@entreprise.mg', 1)
ON CONFLICT DO NOTHING;

INSERT INTO depot(id, nom, adresse, id_site, id_methode_valorisation_stock) VALUES
(1, 'Depot Antananarivo', 'Ankadifotsy', 1, 1),
(2, 'Depot Toamasina', 'Port de Toamasina', 1, 3)
ON CONFLICT DO NOTHING;

-- FOURNISSEURS & CLIENTS
INSERT INTO fournisseur(id, nom, adresse) VALUES
(1, 'Fournisseur Antananarivo', '10 Rue de l''Indépendance, Antananarivo'),
(2, 'Fournisseur Tamatave', '22 Rue du Port, Toamasina')
ON CONFLICT DO NOTHING;

INSERT INTO client(id, nom, adresse) VALUES
(1, 'Client RAKOTO', 'Lot I R Anosy, Antananarivo'),
(2, 'Client RABEARISON', 'Quartier Ambalamanasy, Toamasina')
ON CONFLICT DO NOTHING;

-- UTILISATEUR ADMIN (exemple)
INSERT INTO utilisateur(id, nom, mdp) VALUES
(1, 'tovo', 'tovo123')
ON CONFLICT DO NOTHING;

-- ARTICLES (12 articles de test)
INSERT INTO article(id, reference, designation, prix_achat_ref, prix_vente_ref, id_unite, article_categorie_id) VALUES
(1, 'ART-0001', 'Vis M6x20 (boîte 100)', 2.00, 3.50, 1, 2),
(2, 'ART-0002', 'Ecrou M6 (boîte 100)', 1.00, 1.80, 1, 2),
(3, 'ART-0003', 'Tôle acier 1mm (mètre)', 5.00, 7.50, 4, 1),
(4, 'ART-0004', 'Peinture rouge (1L)', 8.50, 12.00, 3, 2),
(5, 'ART-0005', 'Colle industrielle (500ml)', 6.00, 9.50, 3, 2),
(6, 'ART-0006', 'Carton 50x50 (unité)', 0.80, 1.50, 1, 3),
(7, 'ART-0007', 'Câble RJ45 2m', 2.50, 4.00, 1, 1),
(8, 'ART-0008', 'Switch 8 ports', 25.00, 40.00, 1, 1),
(9, 'ART-0009', 'Batterie 12V', 10.00, 15.00, 1, 1),
(10, 'ART-0010', 'Fil électrique 1mm² (m)', 0.50, 0.90, 4, 1),
(11, 'ART-0011', 'Gants de protection (paire)', 1.20, 2.50, 1, 2),
(12, 'ART-0012', 'Lubrifiant spray 400ml', 4.00, 6.50, 1, 2)
ON CONFLICT DO NOTHING;

-- STOCK INITIAL (quantités par dépôt)
-- On met des quantités de départ raisonnables
INSERT INTO stock(id, article_id, depot_id, quantite_actuelle, date_maj) VALUES
(1, 1, 1, 120.00, NOW()),
(2, 1, 2, 30.00, NOW()),
(3, 2, 1, 200.00, NOW()),
(4, 3, 1, 50.00, NOW()),
(5, 4, 1, 80.00, NOW()),
(6, 5, 1, 60.00, NOW()),
(7, 6, 1, 500.00, NOW()),
(8, 7, 2, 40.00, NOW()),
(9, 8, 2, 10.00, NOW()),
(10, 9, 1, 25.00, NOW()),
(11, 10, 1, 1000.00, NOW()),
(12, 11, 1, 120.00, NOW()),
(13, 12, 1, 75.00, NOW())
ON CONFLICT DO NOTHING;

-- MOUVEMENTS DE STOCK (très nombreux pour tests)
-- We'll create a sequence of movements for a few articles to simulate operations

-- Movements for ART-0001 (id 1) in Depot 1
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(1, NOW() - interval '20 days', 'ENTREE', 0.00, 100.00, 0.00, 100.00, 2.10, 1, 1),
(2, NOW() - interval '15 days', 'ENTREE', 100.00, 50.00, 0.00, 150.00, 2.00, 1, 1),
(3, NOW() - interval '10 days', 'SORTIE', 150.00, 0.00, 30.00, 120.00, 2.00, 1, 1),
(4, NOW() - interval '5 days',  'ENTREE', 120.00, 40.00, 0.00, 160.00, 2.20, 1, 1),
(5, NOW() - interval '2 days',  'SORTIE', 160.00, 0.00, 40.00, 120.00, 3.50, 1, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0002 (id 2) in Depot 1
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(6, NOW() - interval '18 days', 'ENTREE', 0.00, 200.00, 0.00, 200.00, 0.95, 2, 1),
(7, NOW() - interval '7 days', 'SORTIE', 200.00, 0.00, 20.00, 180.00, 1.80, 2, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0003 (id 3) in Depot 1
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(8, NOW() - interval '30 days', 'ENTREE', 0.00, 40.00, 0.00, 40.00, 5.20, 3, 1),
(9, NOW() - interval '12 days', 'SORTIE', 40.00, 0.00, 5.00, 35.00, 7.50, 3, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0004 (id 4)
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(10, NOW() - interval '25 days', 'ENTREE', 0.00, 100.00, 0.00, 100.00, 8.00, 4, 1),
(11, NOW() - interval '3 days', 'SORTIE', 100.00, 0.00, 20.00, 80.00, 12.00, 4, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0006 (id 6), large consumption and replenishment
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(12, NOW() - interval '40 days', 'ENTREE', 0.00, 500.00, 0.00, 500.00, 0.80, 6, 1),
(13, NOW() - interval '35 days', 'SORTIE', 500.00, 0.00, 120.00, 380.00, 1.50, 6, 1),
(14, NOW() - interval '20 days', 'SORTIE', 380.00, 0.00, 80.00, 300.00, 1.40, 6, 1),
(15, NOW() - interval '5 days',  'ENTREE', 300.00, 300.00, 0.00, 600.00, 0.85, 6, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0008 (id 8) in Depot 2
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(16, NOW() - interval '45 days', 'ENTREE', 0.00, 10.00, 0.00, 10.00, 24.00, 8, 2),
(17, NOW() - interval '4 days',  'SORTIE', 10.00, 0.00, 2.00, 8.00, 40.00, 8, 2)
ON CONFLICT DO NOTHING;

-- Movements for ART-0009 (id 9) in Depot 1
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(18, NOW() - interval '10 days', 'ENTREE', 0.00, 25.00, 0.00, 25.00, 9.80, 9, 1),
(19, NOW() - interval '1 days', 'SORTIE', 25.00, 0.00, 5.00, 20.00, 15.00, 9, 1)
ON CONFLICT DO NOTHING;

-- Movements for ART-0012 (id 12)
INSERT INTO mouvement_stock(id, date_mouvement, type_mouvement, quantite_stock_avant, quantite_entree, quantite_sortie, quantite_stock_apres, prix_unitaire_mouvement, id_article, id_depot) VALUES
(20, NOW() - interval '14 days', 'ENTREE', 0.00, 50.00, 0.00, 50.00, 4.50, 12, 1),
(21, NOW() - interval '6 days', 'SORTIE', 50.00, 0.00, 10.00, 40.00, 6.50, 12, 1)
ON CONFLICT DO NOTHING;

-- LOTS: liaisons avec mouvements d'entree (mouvement_entree_id)
INSERT INTO lot_stock(id, numero_lot, id_article, id_depot, date_entree, mouvement_entree_id, quantite_initiale, quantite_restante, prix_unitaire_achat) VALUES
(1, 'LOT-1001', 1, 1, NOW() - interval '20 days', 1, 100.00, 100.00, 2.10),
(2, 'LOT-1002', 1, 1, NOW() - interval '15 days', 2, 50.00, 50.00, 2.00),
(3, 'LOT-2001', 2, 1, NOW() - interval '18 days', 6, 200.00, 180.00, 0.95),
(4, 'LOT-6001', 6, 1, NOW() - interval '40 days', 12, 500.00, 380.00, 0.80),
(5, 'LOT-6002', 6, 1, NOW() - interval '5 days', 15, 300.00, 300.00, 0.85),
(6, 'LOT-8001', 8, 2, NOW() - interval '45 days', 16, 10.00, 8.00, 24.00)
ON CONFLICT DO NOTHING;

-- Sorties liées aux lots (détail des prélèvements)
INSERT INTO sortie_lot_detail(id, mouvement_sortie_id, lot_stock_id, quantite_prelevee, prix_unitaire_lot) VALUES
(1, 3, 1, 30.00, 2.10),
(2, 5, 2, 40.00, 2.00),
(3, 7, 3, 20.00, 0.95),
(4, 9, 3, 5.00, 0.95),
(5, 19, 1, 5.00, 2.10),
(6, 17, 6, 2.00, 24.00),
(7, 11, 4, 20.00, 8.00),
(8, 21, 5, 10.00, 4.50)
ON CONFLICT DO NOTHING;

-- DEMANDES D'ACHAT ET DETAILS (exemples)
INSERT INTO demande_achat(id, numero_da, date_demande, id_entreprise, id_site, id_depot, date_souhaitee, motif_achat, id_status) VALUES
(1, 'DA-0001', CURRENT_DATE - 30, 1, 1, 1, CURRENT_DATE - 15, 'Renouvellement visserie', 3),
(2, 'DA-0002', CURRENT_DATE - 20, 1, 1, 1, CURRENT_DATE - 5, 'Approvisionnement cartons', 2)
ON CONFLICT DO NOTHING;

INSERT INTO demande_achat_details(id, id_demande_achat, id_article, quantite_demandee, prix_unitaire) VALUES
(1, 1, 1, 200.00, 2.00),
(2, 1, 2, 300.00, 0.95),
(3, 2, 6, 1000.00, 0.80)
ON CONFLICT DO NOTHING;

-- PROFORMA FOURNISSEUR
INSERT INTO proforma_fournisseur(id, numero_proforma, date_emission, date_validite, id_fournisseur, montant_ttc, id_demande_achat, id_status) VALUES
(1, 'PF-1001', CURRENT_DATE - 28, CURRENT_DATE + 30, 1, 1200.00, 1, 3)
ON CONFLICT DO NOTHING;

INSERT INTO proforma_fournisseur_details(id, id_proforma_fournisseur, id_article, quantite, prix_unitaire) VALUES
(1, 1, 1, 200.00, 2.00),
(2, 1, 2, 300.00, 0.95)
ON CONFLICT DO NOTHING;

-- BONS DE COMMANDE D'ACHAT
INSERT INTO bon_commande_achat(id, numero_bc, date_commande, id_proforma_fournisseur, id_status, montant_ttc) VALUES
(1, 'BC-A-1001', CURRENT_DATE - 25, 1, 3, 1200.00)
ON CONFLICT DO NOTHING;

INSERT INTO bon_commande_achat_details(id, id_bon_commande_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 1, 200.00, 2.00),
(2, 1, 2, 300.00, 0.95)
ON CONFLICT DO NOTHING;

-- FACTURES D'ACHAT (exemple)
INSERT INTO facture_achat(id, numero_facture_fournisseur, date_facture, id_bon_commande_achat, id_status, montant_ttc) VALUES
(1, 'FA-2001', CURRENT_DATE - 20, 1, 3, 1200.00)
ON CONFLICT DO NOTHING;

INSERT INTO facture_achat_details(id, id_facture_achat, id_article, quantite, prix_unitaire) VALUES
(1, 1, 1, 200.00, 2.00),
(2, 1, 2, 300.00, 0.95)
ON CONFLICT DO NOTHING;

-- DEMANDES, PROFORMAS, BC, FACTURES DE VENTE (exemples simples)
INSERT INTO demande_achat_client(id, numero_da, date_demande, id_client, date_souhaitee, id_status) VALUES
(1, 'DAC-0001', CURRENT_DATE - 10, 1, CURRENT_DATE + 5, 2)
ON CONFLICT DO NOTHING;

INSERT INTO demande_achat_client_details(id, id_demande_achat_client, id_article, quantite_demandee, prix_unitaire) VALUES
(1, 1, 8, 2.00, 40.00)
ON CONFLICT DO NOTHING;

INSERT INTO proforma_vente(id, numero_proforma, date_proforma, id_status, montant_ttc, id_demande_achat_client) VALUES
(1, 'PFV-1001', CURRENT_DATE - 9, 2, 80.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO bon_commande_vente(id, numero_bc, date_commande, id_proforma_vente, id_client, id_status, montant_ttc) VALUES
(1, 'BCV-1001', CURRENT_DATE - 5, 1, 1, 2, 80.00)
ON CONFLICT DO NOTHING;

INSERT INTO bon_commande_vente_details(id, id_bon_commande_vente, id_article, quantite, prix_unitaire) VALUES
(1, 1, 8, 2.00, 40.00)
ON CONFLICT DO NOTHING;

INSERT INTO facture_vente(id, numero_facture, date_facture, id_bon_commande_vente, id_status, montant_ttc, reste_a_payer) VALUES
(1, 'FV-1001', CURRENT_DATE - 3, 1, 2, 80.00, 80.00)
ON CONFLICT DO NOTHING;

INSERT INTO facture_vente_details(id, id_facture_vente, id_article, quantite, prix_unitaire) VALUES
(1, 1, 8, 2.00, 40.00)
ON CONFLICT DO NOTHING;

-- CAISSE & PAIEMENTS (exemples)
INSERT INTO caisse(id, code_caisse, libelle, solde_actuel, entreprise_id) VALUES
(1, 'CAISSE-MG-01', 'Caisse Principale', 3800000.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO caisse_mouvement(id, date_mouvement, libelle_operation, montant_entree, montant_sortie, solde_avant, solde_apres, id_caisse) VALUES
(1, NOW() - interval '2 days', 'Encaissement facture FV-1001', 80.00, 0.00, 920.00, 1000.00, 1)
ON CONFLICT DO NOTHING;

INSERT INTO paiement_vente(id, numero_recu, date_paiement, id_facture_vente, id_caisse_mouvement, montant_total_paye) VALUES
(1, 'REC-0001', CURRENT_DATE - 1, 1, 1, 80.00)
ON CONFLICT DO NOTHING;

INSERT INTO paiement_vente_details(id, id_paiement_vente, montant) VALUES
(1, 1, 80.00)
ON CONFLICT DO NOTHING;

-- Petits ajustements pour que les quantités de stock reflètent les mouvements ci-dessus
UPDATE stock SET quantite_actuelle = 120.00 WHERE article_id = 1 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 180.00 WHERE article_id = 2 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 35.00 WHERE article_id = 3 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 80.00 WHERE article_id = 4 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 600.00 WHERE article_id = 6 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 8.00 WHERE article_id = 8 AND depot_id = 2;
UPDATE stock SET quantite_actuelle = 20.00 WHERE article_id = 9 AND depot_id = 1;
UPDATE stock SET quantite_actuelle = 40.00 WHERE article_id = 12 AND depot_id = 1;

COMMIT;

-- Notes:
--  - Ce script utilise des ids explicites pour faciliter les références entre tables dans un jeu de test.
--  - Si vous exécutez ces scripts plusieurs fois sur la même base, certains INSERTs peuvent échouer selon les contraintes d'unicité; on utilise "ON CONFLICT DO NOTHING" pour limiter les erreurs.
--  - Ajustez les dates/prix/quantités selon vos besoins pour des scénarios de test particuliers.
