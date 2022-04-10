-- liquibase formatted sql

-- changeset patryk:1649589940751-1
ALTER TABLE role
    ADD default_role BOOLEAN DEFAULT FALSE;

-- changeset patryk:1649589940751-2
UPDATE role
SET default_role = FALSE
WHERE default_role IS NULL;

-- changeset patryk:1649589940751-3
ALTER TABLE role
    ALTER COLUMN default_role SET NOT NULL;

