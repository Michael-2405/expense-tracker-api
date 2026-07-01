CREATE TABLE users (
    id              UUID            NOT NULL,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP(6)    WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6)    WITH TIME ZONE NOT NULL,
    deleted_at      TIMESTAMP(6)    WITH TIME ZONE,

    CONSTRAINT      pk_users        PRIMARY KEY (id),
    CONSTRAINT      uk_users_email  UNIQUE (email)
);
