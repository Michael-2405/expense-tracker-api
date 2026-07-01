CREATE TABLE categories (
    id          UUID            NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    color       VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP(6)    WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP(6)    WITH TIME ZONE NOT NULL,
    deleted_at  TIMESTAMP(6)    WITH TIME ZONE,

    CONSTRAINT  pk_categories    PRIMARY KEY (id),
    CONSTRAINT  uk_category_name UNIQUE (name)
);
