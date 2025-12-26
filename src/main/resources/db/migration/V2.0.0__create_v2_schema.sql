-- V2 Schema: New data model with audit trail support

-- Sequences for V2 entities
CREATE SEQUENCE CooperateurV2_SEQ START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE CooperateurRelationship_SEQ START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE PartTransaction_SEQ START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE StatusHistory_SEQ START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE AuditLog_SEQ START WITH 1 INCREMENT BY 50;

-- CooperateurV2: Main member table with new structure
CREATE TABLE CooperateurV2 (
    id BIGINT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    dateNaissance VARCHAR(255),
    telephone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(255) NOT NULL,
    codePostal VARCHAR(255) NOT NULL,
    etudiantOuMinimasSociaux BOOLEAN,
    nombreDePersonnesDansLeFoyer BIGINT CHECK (nombreDePersonnesDansLeFoyer >= 1),
    acceptationDesStatus BOOLEAN NOT NULL,
    paymentStatus VARCHAR(255),
    memberStatus VARCHAR(255),
    source VARCHAR(255),
    notes TEXT,
    stripeSessionId VARCHAR(255),
    createdAt TIMESTAMP,
    updatedAt TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_cooperateurv2_email ON CooperateurV2 (email);

-- CooperateurRelationship: Pair/binome links between cooperateurs
CREATE TABLE CooperateurRelationship (
    id BIGINT NOT NULL,
    cooperateur1_id BIGINT NOT NULL,
    cooperateur2_id BIGINT NOT NULL,
    relationshipType VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_cooperateur_relationship UNIQUE (cooperateur1_id, cooperateur2_id),
    CONSTRAINT fk_relationship_cooperateur1 FOREIGN KEY (cooperateur1_id) REFERENCES CooperateurV2 (id),
    CONSTRAINT fk_relationship_cooperateur2 FOREIGN KEY (cooperateur2_id) REFERENCES CooperateurV2 (id)
);

-- PartTransaction: Share transaction history
CREATE TABLE PartTransaction (
    id BIGINT NOT NULL,
    cooperateur_id BIGINT NOT NULL,
    transactionType VARCHAR(255) NOT NULL,
    parts BIGINT NOT NULL,
    reason VARCHAR(255),
    stripeSessionId VARCHAR(255),
    createdAt TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_parttransaction_cooperateur FOREIGN KEY (cooperateur_id) REFERENCES CooperateurV2 (id)
);

CREATE INDEX idx_parttransaction_cooperateur ON PartTransaction (cooperateur_id);

-- StatusHistory: Status change audit trail
CREATE TABLE StatusHistory (
    id BIGINT NOT NULL,
    cooperateur_id BIGINT NOT NULL,
    statusType VARCHAR(255) NOT NULL,
    previousStatus VARCHAR(255),
    newStatus VARCHAR(255) NOT NULL,
    changedAt TIMESTAMP,
    changedBy VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_statushistory_cooperateur FOREIGN KEY (cooperateur_id) REFERENCES CooperateurV2 (id)
);

CREATE INDEX idx_statushistory_cooperateur ON StatusHistory (cooperateur_id);

-- AuditLog: General entity audit log
CREATE TABLE AuditLog (
    id BIGINT NOT NULL,
    entityType VARCHAR(255) NOT NULL,
    entityId BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    changes TEXT,
    performedBy VARCHAR(255),
    performedAt TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_auditlog_entitytype ON AuditLog (entityType);
CREATE INDEX idx_auditlog_entityid ON AuditLog (entityId);
CREATE INDEX idx_auditlog_entity ON AuditLog (entityType, entityId);
