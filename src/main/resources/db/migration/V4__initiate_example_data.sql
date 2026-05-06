INSERT INTO gyms (name, address, phone_number)
VALUES
    ('Iron Temple Gym', '12 Steel Street, Warsaw', '+48 500 100 200'),
    ('PowerHouse Fitness', '45 Energy Avenue, Krakow', '+48 500 300 400'),
    ('Flex Zone Club', '78 Motion Road, Gdansk', '+48 500 500 600');

INSERT INTO membership_plans (gym_id, name, type, amount, currency_code, duration_months, max_members)
VALUES
    (1, 'Basic Monthly', 'BASIC', 99.00, 'PLN', 1, 2),
    (1, 'Premium Quarterly', 'PREMIUM', 249.00, 'USD', 3, 15),
    (2, 'Starter Monthly', 'BASIC', 89.00, 'GBP', 1, 25),
    (2, 'Group Training Pass', 'GROUP', 179.00, 'PLN', 1, 12),
    (3, 'Flex Premium', 'PREMIUM', 199.00, 'PLN', 1, 18);

INSERT INTO members (membership_plan_id, full_name, email, start_date, status)
VALUES
    (1, 'Adam Nowak', 'adam.nowak@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (1, 'Marta Kowalska', 'marta.kowalska@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (2, 'Piotr Zielinski', 'piotr.zielinski@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (2, 'Anna Wisniewska', 'anna.wisniewska@example.com', CURRENT_TIMESTAMP, 'CANCELLED'),
    (3, 'Kamil Wozniak', 'kamil.wozniak@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (3, 'Natalia Kaminska', 'natalia.kaminska@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (4, 'Tomasz Lewandowski', 'tomasz.lewandowski@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (4, 'Karolina Dabrowska', 'karolina.dabrowska@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (5, 'Michal Szymanski', 'michal.szymanski@example.com', CURRENT_TIMESTAMP, 'ACTIVE'),
    (5, 'Julia Wojcik', 'julia.wojcik@example.com', CURRENT_TIMESTAMP, 'CANCELLED');