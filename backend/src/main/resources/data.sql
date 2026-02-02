-- Données d'exemple pour l'authentification
INSERT INTO utilisateur (nom, mdp) VALUES ('admin', 'admin') ON CONFLICT (nom) DO NOTHING;