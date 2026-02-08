-- Migration script to add id_status column to proforma_fournisseur table

-- Step 1: Add status codes if they don't exist
INSERT INTO status (code, libelle) VALUES ('ATTENTE', 'En attente') ON CONFLICT (code) DO NOTHING;
INSERT INTO status (code, libelle) VALUES ('ANNULE', 'Annulé') ON CONFLICT (code) DO NOTHING;

-- Step 2: Add the column as nullable first
ALTER TABLE proforma_fournisseur ADD COLUMN IF NOT EXISTS id_status BIGINT;

-- Step 3: Set default value for existing rows (use VALIDE status)
UPDATE proforma_fournisseur 
SET id_status = (SELECT id FROM status WHERE code = 'VALIDE' LIMIT 1) 
WHERE id_status IS NULL;

-- Step 4: Add foreign key constraint
ALTER TABLE proforma_fournisseur 
DROP CONSTRAINT IF EXISTS fk_proforma_status;

ALTER TABLE proforma_fournisseur 
ADD CONSTRAINT fk_proforma_status 
FOREIGN KEY (id_status) REFERENCES status(id);

-- Optional: Make the column NOT NULL after data is filled
-- ALTER TABLE proforma_fournisseur ALTER COLUMN id_status SET NOT NULL;
