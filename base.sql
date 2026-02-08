\c postgres;
DROP DATABASE IF EXISTS gestion_stock;
CREATE DATABASE gestion_stock;
\c gestion_stock;

CREATE TABLE utilisateur ( /* Table des administrateurs */
    id       SERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    mdp VARCHAR(100) NOT NULL
);

CREATE TABLE status ( /* Tous les status */
    id      SERIAL PRIMARY KEY,
    code    VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    niveau  INTEGER DEFAULT 0
);

CREATE TABLE unite ( /* Toutes les unités de mesure */
    id      SERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(50)
);

CREATE TABLE article_categorie ( /* Catégories d'articles */
    id      SERIAL PRIMARY KEY,
    code    VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE methode_valorisation_stock ( /* Fifo, Lifo, Cmup */
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(20) UNIQUE NOT NULL,
    libelle     VARCHAR(100),
    description TEXT
);

-- -----------------------------------------------------------------------------
-- 3. STRUCTURE ORGANISATIONNELLE
-- -----------------------------------------------------------------------------

CREATE TABLE groupe ( /* Groupes d'entreprises */
    id          SERIAL PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL,
    description TEXT
);

CREATE TABLE entreprise ( /* Entreprises / Filiales */
    id                          SERIAL PRIMARY KEY,
    nom                         VARCHAR(200) NOT NULL,
    adresse                     VARCHAR(200),
    id_groupe                   INTEGER,
    date_creation   TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_groupe) REFERENCES groupe(id) ON DELETE SET NULL
);

CREATE TABLE site ( /* Sites des entreprises */
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    adresse       VARCHAR(200),
    telephone     VARCHAR(50),
    email         VARCHAR(100),
    id_entreprise INTEGER NOT NULL,
    date_creation TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_entreprise) REFERENCES entreprise(id) ON DELETE CASCADE
);

CREATE TABLE depot ( /* Dépôts rattachés aux sites */
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    adresse       VARCHAR(200),
    id_site       INTEGER NOT NULL,
    id_methode_valorisation_stock INTEGER,
    date_creation TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_site) REFERENCES site(id) ON DELETE CASCADE,
    FOREIGN KEY (id_methode_valorisation_stock) REFERENCES methode_valorisation_stock(id)
);

CREATE TABLE fournisseur (
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    adresse       VARCHAR(200)
);

CREATE TABLE client (
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(200) NOT NULL,
    adresse       VARCHAR(200)
);

-- -----------------------------------------------------------------------------
-- 4. ARTICLES & STOCK
-- -----------------------------------------------------------------------------

CREATE TABLE article (
    id                  SERIAL PRIMARY KEY,
    reference           VARCHAR(50) UNIQUE NOT NULL,
    designation         VARCHAR(200) NOT NULL,
    prix_achat_ref      NUMERIC(15,2) DEFAULT 0,
    prix_vente_ref      NUMERIC(15,2) DEFAULT 0,
    id_unite            INTEGER NOT NULL,
    article_categorie_id INTEGER NOT NULL,
    FOREIGN KEY (id_unite)            REFERENCES unite(id),
    FOREIGN KEY (article_categorie_id) REFERENCES article_categorie(id)
);

CREATE TABLE stock (
    id                        SERIAL PRIMARY KEY,
    article_id                INTEGER NOT NULL,
    depot_id                  INTEGER NOT NULL,
    quantite_actuelle         NUMERIC(15,2) DEFAULT 0,
    date_maj                  TIMESTAMP DEFAULT NOW(),
    UNIQUE (article_id, depot_id),
    FOREIGN KEY (article_id)                REFERENCES article(id),
    FOREIGN KEY (depot_id)                  REFERENCES depot(id)
);

CREATE TABLE mouvement_stock (
    id                      SERIAL PRIMARY KEY,
    date_mouvement          TIMESTAMP DEFAULT NOW(),
    type_mouvement          VARCHAR(20) NOT NULL,
    quantite_stock_avant    NUMERIC(15,2) NOT NULL DEFAULT 0,
    quantite_entree         NUMERIC(15,2) DEFAULT 0,
    quantite_sortie         NUMERIC(15,2) DEFAULT 0,
    quantite_stock_apres    NUMERIC(15,2) NOT NULL DEFAULT 0,
    prix_unitaire_mouvement NUMERIC(15,2),
    id_article              INTEGER NOT NULL,
    id_depot                INTEGER NOT NULL,
    FOREIGN KEY (id_article)   REFERENCES article(id),
    FOREIGN KEY (id_depot)     REFERENCES depot(id)
);

CREATE TABLE lot_stock (
    id                  SERIAL PRIMARY KEY,
    numero_lot          VARCHAR(50) UNIQUE NOT NULL,
    id_article          INTEGER NOT NULL,
    id_depot            INTEGER NOT NULL,
    date_entree         TIMESTAMP DEFAULT NOW(),
    mouvement_entree_id INTEGER,
    quantite_initiale   NUMERIC(15,2) NOT NULL,
    quantite_restante   NUMERIC(15,2) NOT NULL,
    prix_unitaire_achat NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_article)          REFERENCES article(id),
    FOREIGN KEY (id_depot)            REFERENCES depot(id),
    FOREIGN KEY (mouvement_entree_id) REFERENCES mouvement_stock(id)
);

CREATE TABLE sortie_lot_detail (
    id                  SERIAL PRIMARY KEY,
    mouvement_sortie_id INTEGER NOT NULL,
    lot_stock_id        INTEGER NOT NULL,
    quantite_prelevee   NUMERIC(15,2) NOT NULL,
    prix_unitaire_lot   NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (mouvement_sortie_id) REFERENCES mouvement_stock(id) ON DELETE CASCADE,
    FOREIGN KEY (lot_stock_id)        REFERENCES lot_stock(id)
);

-- -----------------------------------------------------------------------------
-- 5. ACHATS
-- -----------------------------------------------------------------------------

CREATE TABLE demande_achat (
    id                    SERIAL PRIMARY KEY,
    numero_da             VARCHAR(50) UNIQUE NOT NULL,
    date_demande          DATE DEFAULT CURRENT_DATE,
    id_entreprise         INTEGER NOT NULL,
    id_site               INTEGER NOT NULL,
    id_depot              INTEGER,
    date_souhaitee        DATE,
    motif_achat           TEXT,
    id_status             INTEGER NOT NULL,
    date_creation         TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_entreprise)         REFERENCES entreprise(id),
    FOREIGN KEY (id_depot)        REFERENCES depot(id), 
    FOREIGN KEY (id_status)             REFERENCES status(id)
);

CREATE TABLE demande_achat_details (
    id                        SERIAL PRIMARY KEY,
    id_demande_achat INTEGER NOT NULL,
    id_article                INTEGER NOT NULL,
    quantite_demandee         NUMERIC(15,2) NOT NULL,
    prix_unitaire               NUMERIC(15,2) DEFAULT 0,
    FOREIGN KEY (id_demande_achat) REFERENCES demande_achat(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)       REFERENCES article(id)
);

CREATE TABLE proforma_fournisseur (
    id                      SERIAL PRIMARY KEY,
    numero_proforma         VARCHAR(50) NOT NULL,
    date_emission           DATE DEFAULT CURRENT_DATE,
    date_validite           DATE,
    id_fournisseur INTEGER NOT NULL,
    montant_ttc             NUMERIC(15,2) DEFAULT 0,
    id_demande_achat INTEGER,
    FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id),
    FOREIGN KEY (id_demande_achat) REFERENCES demande_achat(id)
);

ALTER TABLE proforma_fournisseur ADD column id_status INTEGER NOT NULL;
ALTER TABLE proforma_fournisseur ADD FOREIGN KEY (id_status) REFERENCES status(id);

CREATE TABLE proforma_fournisseur_details (
    id                   SERIAL PRIMARY KEY,
    id_proforma_fournisseur INTEGER NOT NULL,
    id_article           INTEGER NOT NULL,
    quantite             NUMERIC(15,2) NOT NULL,
    prix_unitaire        NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_proforma_fournisseur) REFERENCES proforma_fournisseur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)              REFERENCES article(id)
);

CREATE TABLE bon_commande_achat (
    id                    SERIAL PRIMARY KEY,
    numero_bc             VARCHAR(50) UNIQUE NOT NULL,
    date_commande         DATE DEFAULT CURRENT_DATE,
    id_proforma_fournisseur INTEGER,
    id_status             INTEGER NOT NULL,
    montant_ttc           NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_proforma_fournisseur)   REFERENCES proforma_fournisseur(id),
    FOREIGN KEY (id_status) REFERENCES status(id)
);

CREATE TABLE bon_commande_achat_details (
    id                   SERIAL PRIMARY KEY,
    id_bon_commande_achat INTEGER NOT NULL,
    id_article           INTEGER NOT NULL,
    quantite             NUMERIC(15,2) NOT NULL,
    prix_unitaire        NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_bon_commande_achat) REFERENCES bon_commande_achat(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)            REFERENCES article(id)
);

CREATE TABLE facture_achat (
    id                       SERIAL PRIMARY KEY,
    numero_facture_fournisseur VARCHAR(50) NOT NULL,
    date_facture             DATE DEFAULT CURRENT_DATE,
    id_bon_commande_achat    INTEGER,
    id_status                INTEGER NOT NULL,
    id_client INTEGER NOT NULL,
    montant_ttc              NUMERIC(15,2) NOT NULL,
    reste_a_payer            NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_bon_commande_achat)    REFERENCES bon_commande_achat(id),
    FOREIGN KEY (id_status)                REFERENCES status(id),
    FOREIGN KEY (id_client) REFERENCES client(id)
);

ALTER TABLE facture_achat DROP COLUMN id_client;
ALTER TABLE facture_achat DROP COLUMN reste_a_payer;

CREATE TABLE facture_achat_details (
    id              SERIAL PRIMARY KEY,
    id_facture_achat INTEGER NOT NULL,
    id_article      INTEGER NOT NULL,
    quantite        NUMERIC(15,2) NOT NULL,
    prix_unitaire   NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_facture_achat) REFERENCES facture_achat(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)       REFERENCES article(id)
);

-- -----------------------------------------------------------------------------
-- 6. VENTES
-- -----------------------------------------------------------------------------
CREATE TABLE demande_achat_client (
    id                    SERIAL PRIMARY KEY,
    numero_da             VARCHAR(50) UNIQUE NOT NULL,
    date_demande          DATE DEFAULT CURRENT_DATE,
    id_client INTEGER NOT NULL,
    date_souhaitee        DATE,
    id_status             INTEGER NOT NULL,
    date_creation         TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_status) REFERENCES status(id)
);

CREATE TABLE demande_achat_client_details (
    id                        SERIAL PRIMARY KEY,
    id_demande_achat_client INTEGER NOT NULL,
    id_article                INTEGER NOT NULL,
    quantite_demandee         NUMERIC(15,2) NOT NULL,
    prix_unitaire               NUMERIC(15,2) DEFAULT 0,
    FOREIGN KEY (id_demande_achat_client) REFERENCES demande_achat_client(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)       REFERENCES article(id)
);

CREATE TABLE proforma_vente (
    id                 SERIAL PRIMARY KEY,
    numero_proforma       VARCHAR(50) UNIQUE NOT NULL,
    date_proforma         DATE DEFAULT CURRENT_DATE,
    id_status          INTEGER NOT NULL,
    montant_ttc        NUMERIC(15,2),
    FOREIGN KEY (id_status) REFERENCES status(id)
);
ALTER TABLE proforma_vente ADD COLUMN id_demande_achat_client INTEGER NOT NULL;

ALTER TABLE proforma_vente
ADD CONSTRAINT fk_proforma_demande
FOREIGN KEY (id_demande_achat_client)
REFERENCES demande_achat_client(id);

CREATE TABLE proforma_vente_details (
    id           SERIAL PRIMARY KEY,
    id_proforma_vente INTEGER NOT NULL,
    id_article   INTEGER NOT NULL,
    quantite     NUMERIC(15,2) NOT NULL,
    prix_unitaire NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_proforma_vente) REFERENCES proforma_vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)     REFERENCES article(id)
);

CREATE TABLE bon_commande_vente (
    id                  SERIAL PRIMARY KEY,
    numero_bc           VARCHAR(50) UNIQUE NOT NULL,
    date_commande       DATE DEFAULT CURRENT_DATE,
    id_proforma_vente   INTEGER,
    id_client INTEGER NOT NULL,
    id_status           INTEGER NOT NULL,
    montant_ttc         NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_proforma_vente)      REFERENCES proforma_vente(id),
    FOREIGN KEY (id_client) REFERENCES client(id),
    FOREIGN KEY (id_status) REFERENCES status(id)
);

CREATE TABLE bon_commande_vente_details (
    id                   SERIAL PRIMARY KEY,
    id_bon_commande_vente INTEGER NOT NULL,
    id_article           INTEGER NOT NULL,
    quantite             NUMERIC(15,2) NOT NULL,
    prix_unitaire        NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_bon_commande_vente) REFERENCES bon_commande_vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article)            REFERENCES article(id)
);

CREATE TABLE facture_vente (
    id                  SERIAL PRIMARY KEY,
    numero_facture      VARCHAR(50) UNIQUE NOT NULL,
    date_facture        DATE DEFAULT CURRENT_DATE,
    id_bon_commande_vente INTEGER,
    id_status           INTEGER NOT NULL,
    montant_ttc         NUMERIC(15,2) NOT NULL,
    reste_a_payer       NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_bon_commande_vente)  REFERENCES bon_commande_vente(id),
    FOREIGN KEY (id_status) REFERENCES status(id)
);

CREATE TABLE facture_vente_details (
    id             SERIAL PRIMARY KEY,
    id_facture_vente INTEGER NOT NULL,
    id_article     INTEGER NOT NULL,
    quantite       NUMERIC(15,2) NOT NULL,
    prix_unitaire  NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_facture_vente) REFERENCES facture_vente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article) REFERENCES article(id)
);

-- -----------------------------------------------------------------------------
-- 7. CAISSE & PAIEMENTS
-- -----------------------------------------------------------------------------

CREATE TABLE caisse (
    id          SERIAL PRIMARY KEY,
    code_caisse VARCHAR(50) UNIQUE,
    libelle     VARCHAR(100) NOT NULL,
    solde_actuel NUMERIC(15,2) DEFAULT 0,
    entreprise_id INTEGER NOT NULL,
    FOREIGN KEY (entreprise_id) REFERENCES entreprise(id)
);

CREATE TABLE caisse_mouvement (
    id              SERIAL PRIMARY KEY,
    date_mouvement  TIMESTAMP DEFAULT NOW(),
    libelle_operation VARCHAR(200),
    montant_entree  NUMERIC(15,2) DEFAULT 0,
    montant_sortie  NUMERIC(15,2) DEFAULT 0,
    solde_avant     NUMERIC(15,2) NOT NULL,
    solde_apres     NUMERIC(15,2) NOT NULL,
    id_caisse       INTEGER NOT NULL,
    FOREIGN KEY (id_caisse)    REFERENCES caisse(id)
);

CREATE TABLE paiement_vente (
    id                  SERIAL PRIMARY KEY,
    numero_recu         VARCHAR(50) UNIQUE,
    date_paiement       DATE DEFAULT CURRENT_DATE,
    id_facture_vente    INTEGER NOT NULL,
    id_caisse_mouvement INTEGER NOT NULL,
    montant_total_paye  NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_facture_vente)    REFERENCES facture_vente(id),
    FOREIGN KEY (id_caisse_mouvement) REFERENCES caisse_mouvement(id)
);

CREATE TABLE paiement_vente_details (
    id               SERIAL PRIMARY KEY,
    id_paiement_vente INTEGER NOT NULL,
    montant           NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_paiement_vente) REFERENCES paiement_vente(id) ON DELETE CASCADE
);

CREATE TABLE paiement_achat (
    id                  SERIAL PRIMARY KEY,
    numero_paiement     VARCHAR(50) UNIQUE,
    date_paiement       DATE DEFAULT CURRENT_DATE,
    id_facture_achat    INTEGER NOT NULL,
    id_caisse_mouvement INTEGER NOT NULL,
    montant_total_paye  NUMERIC(15,2) NOT NULL,
    FOREIGN KEY (id_facture_achat)    REFERENCES facture_achat(id),
    FOREIGN KEY (id_caisse_mouvement) REFERENCES caisse_mouvement(id)
);

CREATE TABLE paiement_achat_details (
    id               SERIAL PRIMARY KEY,
    id_paiement_achat INTEGER NOT NULL,
    montant           NUMERIC(15,2) NOT NULL,
    reference_externe VARCHAR(100),
    FOREIGN KEY (id_paiement_achat) REFERENCES paiement_achat(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------- 
-- VUES
-- -----------------------------------------------------------------------------

CREATE VIEW depot_valorisation AS
SELECT 
    d.id,
    d.nom AS depot_nom,
    d.adresse AS depot_adresse,
    s.nom AS site_nom,
    e.nom AS entreprise_nom,
    m.code AS methode_code,
    m.libelle AS methode_libelle,
    m.description AS methode_description
FROM depot d
JOIN site s ON d.id_site = s.id
JOIN entreprise e ON s.id_entreprise = e.id
JOIN methode_valorisation_stock m ON d.id_methode_valorisation_stock = m.id;