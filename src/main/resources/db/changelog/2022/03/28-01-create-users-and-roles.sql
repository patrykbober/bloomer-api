-- liquibase formatted sql

-- changeset pbober:1648423991084-1
CREATE SEQUENCE IF NOT EXISTS role_id_seq START WITH 1 INCREMENT BY 1;

-- changeset pbober:1648423991084-2
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 INCREMENT BY 1;

-- changeset pbober:1648423991084-3
CREATE TABLE bloomer_user
(
    id              BIGSERIAL                NOT NULL,
    creation_date   TIMESTAMP with time zone NOT NULL,
    update_date     TIMESTAMP with time zone NOT NULL,
    optlock_version BIGINT,
    email           VARCHAR(255)             NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    password        VARCHAR(255)             NOT NULL,
    active          BOOLEAN                  NOT NULL,
    CONSTRAINT pk_bloomer_user PRIMARY KEY (id)
);

-- changeset pbober:1648423991084-4
CREATE TABLE role
(
    id              BIGSERIAL                NOT NULL,
    creation_date   TIMESTAMP with time zone NOT NULL,
    update_date     TIMESTAMP with time zone NOT NULL,
    optlock_version BIGINT,
    name            VARCHAR(255)             NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

-- changeset pbober:1648423991084-5
CREATE TABLE user_has_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_has_roles PRIMARY KEY (role_id, user_id)
);

-- changeset pbober:1648423991084-6
ALTER TABLE bloomer_user
    ADD CONSTRAINT uc_bloomer_user_email UNIQUE (email);

-- changeset pbober:1648423991084-7
ALTER TABLE role
    ADD CONSTRAINT uc_role_name UNIQUE (name);

-- changeset pbober:1648423991084-8
ALTER TABLE user_has_roles
    ADD CONSTRAINT fk_usehasrol_on_bloomer_user FOREIGN KEY (user_id) REFERENCES bloomer_user (id);

-- changeset pbober:1648423991084-9
ALTER TABLE user_has_roles
    ADD CONSTRAINT fk_usehasrol_on_role FOREIGN KEY (role_id) REFERENCES role (id);

