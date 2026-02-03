-- Add uuid column to Cooperateur
ALTER TABLE Cooperateur ADD COLUMN uuid VARCHAR(36);

-- Generate UUIDs for existing data
UPDATE Cooperateur SET uuid = gen_random_uuid()::text WHERE uuid IS NULL;

-- Add NOT NULL and UNIQUE constraints
ALTER TABLE Cooperateur ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE Cooperateur ADD CONSTRAINT cooperateur_uuid_unique UNIQUE (uuid);

-- Add uuid column to SouscriptionSupplementaire
ALTER TABLE SouscriptionSupplementaire ADD COLUMN uuid VARCHAR(36);

-- Generate UUIDs for existing data
UPDATE SouscriptionSupplementaire SET uuid = gen_random_uuid()::text WHERE uuid IS NULL;

-- Add NOT NULL and UNIQUE constraints
ALTER TABLE SouscriptionSupplementaire ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE SouscriptionSupplementaire ADD CONSTRAINT souscription_supplementaire_uuid_unique UNIQUE (uuid);
