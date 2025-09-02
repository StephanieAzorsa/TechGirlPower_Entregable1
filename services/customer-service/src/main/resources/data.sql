CREATE TABLE IF NOT EXISTS customers
(
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(255)        NOT NULL,
    last_name       VARCHAR(255)        NOT NULL,
    dni             VARCHAR(8) UNIQUE   NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    registered_date DATE                NOT NULL
);

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174000',
       'Mario',
       'La Rosa',
       '11111111',
       'mario.larosa@example.com',
       '2025-08-21'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174000');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174001',
       'Maria',
       'Salazar',
       '87654321',
       'maria.salazar@example.com',
       '2025-08-21'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174001');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174002',
       'Mirian',
       'Ore',
       '25478523',
       'mirian.ore@example.com',
       '2025-08-21'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174002');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174003',
       'Karen',
       'Paredes',
       '74526341',
       'karen.paredes@example.com',
       '2025-08-21'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174003');