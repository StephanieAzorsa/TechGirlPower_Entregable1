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
       'Stephanie',
       'Azorsa',
       '12345678',
       'stephanie.azorsa@example.com',
       '2025-09-08'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174000');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174001',
       'Andrea',
       'Molina',
       '11111111',
       'andrea.molina@example.com',
       '2025-09-08'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174001');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174002',
       'Angie',
       'Lona',
       '22222222',
       'angie.loa@example.com',
       '2025-09-08'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174002');

INSERT INTO customers (id, name, last_name, dni, email, registered_date)
SELECT '123e4567-e89b-12d3-a456-426614174003',
       'Aracely',
       'Coronel',
       '33333333',
       'aracely.coronel@example.com',
       '2025-09-08'
WHERE NOT EXISTS (SELECT 1
                  FROM customers
                  WHERE id = '123e4567-e89b-12d3-a456-426614174003');