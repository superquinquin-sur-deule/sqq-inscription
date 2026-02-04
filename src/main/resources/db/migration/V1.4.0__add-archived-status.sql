-- Add ARCHIVED status (ordinal 3) to cooperateur and souscriptionsupplementaire tables

-- Drop existing check constraints and add new ones that include ARCHIVED status

-- For cooperateur table
ALTER TABLE cooperateur DROP CONSTRAINT IF EXISTS cooperateur_status_check;
ALTER TABLE cooperateur ADD CONSTRAINT cooperateur_status_check CHECK (status BETWEEN 0 AND 3);

-- For souscriptionsupplementaire table
ALTER TABLE souscriptionsupplementaire DROP CONSTRAINT IF EXISTS souscriptionsupplementaire_status_check;
ALTER TABLE souscriptionsupplementaire ADD CONSTRAINT souscriptionsupplementaire_status_check CHECK (status BETWEEN 0 AND 3);
