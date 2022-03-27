-- liquibase formatted sql

-- changeset patryk:1648076252674-1
CREATE SEQUENCE IF NOT EXISTS role_id_seq START WITH 1 INCREMENT BY 1;

-- changeset patryk:1648076252674-2
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 INCREMENT BY 1;

-- changeset patryk:1648076252674-3
CREATE TABLE bloomer_user
(
    id              BIGINT       NOT NULL,
    creation_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    optlock_version BIGINT,
    active          BOOLEAN,
    email           VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    password        VARCHAR(255),
    CONSTRAINT "bloomer_userPK" PRIMARY KEY (id)
);

-- changeset patryk:1648076252674-4
CREATE TABLE role
(
    id              BIGINT NOT NULL,
    creation_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    optlock_version BIGINT,
    name            VARCHAR(255),
    CONSTRAINT "rolePK" PRIMARY KEY (id)
);

-- changeset patryk:1648076252674-5
CREATE TABLE user_has_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT user_has_roles_pkey PRIMARY KEY (user_id, role_id)
);

-- changeset patryk:1648076252674-6
ALTER TABLE bloomer_user
    ADD CONSTRAINT "UK_gariowi59l84kdxjmq0lmmu0t" UNIQUE (email);

-- changeset patryk:1648076252674-7
ALTER TABLE user_has_roles
    ADD CONSTRAINT "FKg19yv1lyodwysqhombdwcf5yq" FOREIGN KEY (role_id) REFERENCES role (id);

-- changeset patryk:1648076252674-8
ALTER TABLE user_has_roles
    ADD CONSTRAINT "FKta3dcg4ukk5py2l5dgwn6x5s3" FOREIGN KEY (user_id) REFERENCES bloomer_user (id);

