CREATE TABLE expenses (
    id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    cost DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_expenses PRIMARY KEY (id)
);
