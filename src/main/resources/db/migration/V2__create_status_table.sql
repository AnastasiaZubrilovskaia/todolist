CREATE TABLE status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO status (name) VALUES ('создана'), ('в работе'), ('выполнена'), ('просрочена'), ('отменена');
