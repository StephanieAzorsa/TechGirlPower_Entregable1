INSERT INTO accounts (id, account_number, balance, account_type, customer_id)
SELECT 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
       '1000000001',
       1500.75,
       'AHORROS',
       '123e4567-e89b-12d3-a456-426614174000'
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

INSERT INTO accounts (id, account_number, balance, account_type, customer_id)
SELECT 'a1b2c3d4-e5f6-7890-abcd-ef1234567891',
       '1000000002',
       3200.50,
       'CORRIENTE',
       '123e4567-e89b-12d3-a456-426614174000'
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567891');
