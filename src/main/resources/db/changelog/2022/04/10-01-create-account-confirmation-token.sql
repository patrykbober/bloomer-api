-- liquibase formatted sql

-- changeset patryk:1649545553590-1
CREATE SEQUENCE IF NOT EXISTS account_confirmation_token_id_seq START WITH 1 INCREMENT BY 1;

-- changeset patryk:1649545553590-2
CREATE TABLE account_confirmation_token
(
    id              BIGSERIAL                NOT NULL,
    creation_date   TIMESTAMP with time zone NOT NULL,
    update_date     TIMESTAMP with time zone NOT NULL,
    optlock_version BIGINT,
    user_id         BIGINT                   NOT NULL,
    token           VARCHAR(255)             NOT NULL,
    expiration_date TIMESTAMP with time zone NOT NULL,
    used            BOOLEAN                  NOT NULL,
    CONSTRAINT pk_account_confirmation_token PRIMARY KEY (id)
);

-- changeset patryk:1649545553590-3
ALTER TABLE account_confirmation_token
    ADD CONSTRAINT uc_account_confirmation_token_token UNIQUE (token);

-- changeset patryk:1649545553590-4
ALTER TABLE account_confirmation_token
    ADD CONSTRAINT FK_ACCOUNT_CONFIRMATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES bloomer_user (id);

