CREATE TABLE IF NOT EXISTS departments
(

    id               BIGSERIAL PRIMARY KEY,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMPTZ,
    name             VARCHAR(20)                           NOT NULL UNIQUE,
    description      VARCHAR(100),
    established_date DATE                                  NOT NULL

    );

CREATE TABLE IF NOT EXISTS file_entities
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    name         VARCHAR(200)                          NOT NULL,
    content_type VARCHAR(30)                           NOT NULL,
    size         BIGINT                                NOT NULL

    );

CREATE TABLE IF NOT EXISTS employees
(
    id               BIGSERIAL PRIMARY KEY,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMPTZ,
    name             VARCHAR(20)                           NOT NULL,
    email            VARCHAR(100)                          NOT NULL UNIQUE,
    employee_number  VARCHAR(100)                          NOT NULL UNIQUE,
    department_id    BIGINT                                NOT NULL,
    position         VARCHAR(100)                          NOT NULL,
    hire_date        DATE                                  NOT NULL,
    status           VARCHAR(20)                           NOT NULL,
    profile_image_id BIGINT,

    CHECK ( status IN('ACTIVE', 'ON_LEAVE', 'RESIGNED') ),

    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE NO ACTION,
    FOREIGN KEY (profile_image_id) REFERENCES file_entities (id) ON DELETE SET NULL

    );

CREATE TABLE IF NOT EXISTS change_logs
(

    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMPTZ          DEFAULT CURRENT_TIMESTAMP NOT NULL,
    employee_id BIGINT      NOT NULL,
    type        VARCHAR(20) NOT NULL,
    memo        text,
    ip_address  VARCHAR(20) NOT NULL DEFAULT '127.0.0.1',

    CHECK (type IN ('CREATED', 'UPDATED', 'DELETED') ),

    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE NO ACTION


    );

CREATE TABLE IF NOT EXISTS change_detail_logs
(

    id            BIGSERIAL PRIMARY KEY,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    change_log_id BIGINT                                NOT NULL,
    property_name varchar(30)                           NOT NULL,
    before        TEXT,
    after         TEXT,

    CHECK (property_name IN ('NAME', 'EMAIL', 'DEPARTMENT', 'POSITION', 'HIRE_DATE', 'STATUS', 'PROFILE')),

    FOREIGN KEY (change_log_id) REFERENCES change_logs (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS backups
(

    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    worker     VARCHAR(20)                           NOT NULL,
    started_at TIMESTAMPTZ                           NOT NULL,
    ended_at   TIMESTAMPTZ,
    status     VARCHAR(20)                           NOT NULL,
    file_id    BIGINT,

    CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'SKIPPED')),

    FOREIGN KEY (file_id) REFERENCES file_entities (id) ON DELETE SET NULL

    );







