CREATE TABLE IF NOT EXISTS waiter_account (
                                              waiter_id BIGSERIAL PRIMARY KEY NOT NULL,
                                              name VARCHAR NOT NULL,
                                              employment_date TIMESTAMPTZ NOT NULL,
                                              sex VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS waiter_order (
                                            order_no BIGSERIAL PRIMARY KEY NOT NULL,
                                            status VARCHAR NOT NULL,
                                            create_dttm TIMESTAMPTZ NOT NULL,
                                            waiter_id BIGINT NOT NULL,
                                            table_no VARCHAR NOT NULL,
                                            CONSTRAINT fk_waiter
                                            FOREIGN KEY (waiter_id)
    REFERENCES waiter_account (waiter_id)
    );

CREATE TABLE IF NOT EXISTS payment (
                                       order_no BIGSERIAL PRIMARY KEY NOT NULL,
                                       payment_type VARCHAR,
                                       payment_date TIMESTAMPTZ,
                                       payment_sum FLOAT,
                                       CONSTRAINT fk_order
                                       FOREIGN KEY (order_no)
    REFERENCES waiter_order (order_no)
    );

CREATE TABLE IF NOT EXISTS menu (
                                    id BIGSERIAL PRIMARY KEY NOT NULL,
                                    dish_name VARCHAR NOT NULL,
                                    dish_cost FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_positions (
                                               composition_id BIGSERIAL PRIMARY KEY NOT NULL,
                                               dish_num BIGINT NOT NULL,
                                               order_no BIGINT NOT NULL,
                                               menu_position_id BIGINT NOT NULL,
                                               CONSTRAINT fk_order_no
                                               FOREIGN KEY (order_no)
    REFERENCES waiter_order (order_no),
    CONSTRAINT fk_menu_position
    FOREIGN KEY (menu_position_id)
    REFERENCES menu (id)
    );

INSERT INTO waiter_account (name, employment_date, sex)
VALUES
('Иван Петров', '2023-01-15 09:00:00+03', 'MALE'),
('Анна Иванова', '2022-06-01 10:30:00+03', 'FEMALE');

-- Создание тестового меню
INSERT INTO menu (dish_name, dish_cost)
VALUES
('Пицца Маргарита', 12.50),
('Спагетти Карбонара', 10.75),
('Салат Цезарь', 8.90);

-- Создание заказов
INSERT INTO waiter_order (status, create_dttm, waiter_id, table_no)
VALUES
-- 2 заказа без позиций
('PREPARING', NOW() - INTERVAL '2 HOUR', 1, 'A12'),
('PREPARING', NOW() - INTERVAL '1 HOUR', 2, 'B7'),
-- Остальные заказы
('PREPARING', NOW(), 1, 'C3'),
('READY', NOW() - INTERVAL '30 MINUTES', 2, 'D5'),
('PAID_AWAITING_SERVING', NOW() - INTERVAL '15 MINUTES', 1, 'E9'),
('SENT_TO_KITCHEN', NOW() - INTERVAL '10 MINUTES', 2, 'F2'),
('COOKING', NOW() - INTERVAL '5 MINUTES', 1, 'G11');

-- Создание позиций для заказов (начиная с id=3)
INSERT INTO order_positions (dish_num, order_no, menu_position_id)
VALUES
-- Для заказа 3 (PREPARING)
(2, 3, 1),
(1, 3, 3),
-- Для заказа 4 (READY)
(3, 4, 2),
-- Для заказа 5 (PAID_AWAITING_SERVING)
(2, 5, 1),
-- Для заказа 6 (SENT_TO_KITCHEN)
(1, 6, 3),
-- Для заказа 7 (COOKING)
(2, 7, 2);

-- Создание тестовых платежей
INSERT INTO payment (order_no, payment_type, payment_date, payment_sum)
VALUES
(5, 'CARD', NOW() - INTERVAL '15 MINUTES', 25.00);