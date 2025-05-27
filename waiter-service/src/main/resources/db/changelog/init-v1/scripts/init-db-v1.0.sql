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

-- Вставка тестовых данных в таблицу waiter_account
INSERT INTO waiter_account (name, employment_date, sex)
VALUES
    ('test', '2025-04-06 13:44:58.760000+00:00', 'MALE');

INSERT INTO menu (id, dish_name, dish_cost)
VALUES
    (1, 'test1', 100.25),
    (2, 'test2', 200.5);