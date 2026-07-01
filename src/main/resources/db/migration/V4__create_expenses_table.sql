CREATE TABLE expenses (
    id              UUID            NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    cost            DECIMAL(19, 2)  NOT NULL,
    currency        VARCHAR(3)      NOT NULL,
    expense_date    DATE            NOT NULL,
    user_id         UUID            NOT NULL,
    category_id     UUID            NOT NULL,
    created_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    -- Constraints
    CONSTRAINT pk_expenses                  PRIMARY KEY (id),
    CONSTRAINT fk_expenses_user             FOREIGN KEY (user_id)       REFERENCES users(id),
    CONSTRAINT fk_expenses_category         FOREIGN KEY (category_id)   REFERENCES  categories(id),
    CONSTRAINT chk_expenses_cost_positive   CHECK ( cost > 0),
    CONSTRAINT chk_expenses_currency        CHECK ( currency IN ('USD', 'EUR', 'DOP'))
);

CREATE INDEX idx_expenses_user
ON expenses(user_id);

CREATE INDEX idx_expenses_user_date
ON expenses(user_id, expense_date);

CREATE INDEX idx_expenses_user_category
ON expenses(user_id, category_id);
