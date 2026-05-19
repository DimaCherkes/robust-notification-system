CREATE TABLE users
(
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(30) NOT NULL UNIQUE,
    password            VARCHAR(80) NOT NULL,
    email               VARCHAR(50) UNIQUE,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_status VARCHAR(30) NOT NULL,
    last_login          TIMESTAMP,
    deleted             BOOLEAN     NOT NULL DEFAULT false
);

CREATE TABLE roles
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(50) NOT NULL,
    user_system_role VARCHAR(64) NOT NULL,
    active           BOOLEAN     NOT NULL DEFAULT true,
    created_by       VARCHAR(50) NOT NULL
);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id INT    NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE refresh_tokens
(
    id      SERIAL PRIMARY KEY,
    token   VARCHAR(128) NOT NULL,
    created TIMESTAMP    NOT NULL DEFAULT CURRENT_tIMESTAMP,
    user_id BIGINT       NOT NULL,
    CONSTRAINT FK_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT refresh_token_UNIQUE UNIQUE (user_id, id)
);

INSERT INTO users(username, password, email, created_at, updated_at, registration_status, last_login, deleted)
VALUES ('super_admin', '$2y$05$kH.nHnyrWF6uVwGGTFY0QesVrg57k4rAmMgwRS7h2YSc2euriWv5S', 'super_admin@gmail.com',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE',
        CURRENT_TIMESTAMP, false),
       ('admin', '$2y$05$u6gG38dad79gMcJanxX.NekWh1F1yIcRZmdWLpEqC5kkH0PgeLWTa', 'admin@gmail.com',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE',
        CURRENT_TIMESTAMP, false),
       ('user', '$2y$05$Dw4urlwxqDNdsIr9QOv0wuWvqFVQZk.Wto3vCpO0EIjH1WtfmT6f6', 'user@gmail.com',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE',
        CURRENT_TIMESTAMP, false);

INSERT INTO roles (name, user_system_role, created_by)
VALUES ('SUPER_ADMIN', 'SUPER_ADMIN', 'SUPER_ADMIN'),
       ('ADMIN', 'ADMIN', 'SUPER_ADMIN'),
       ('USER', 'USER', 'SUPER_ADMIN');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);
