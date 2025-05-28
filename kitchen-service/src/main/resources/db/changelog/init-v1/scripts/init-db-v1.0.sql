CREATE TABLE IF NOT EXISTS dish (
                                    dish_id BIGINT PRIMARY KEY NOT NULL,
                                    balance BIGINT NOT NULL,
                                    short_name VARCHAR NOT NULL,
                                    dish_composition VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS kitchen_order (
                                             kitchen_order_id BIGINT PRIMARY KEY NOT NULL,
                                             waiter_order_no BIGINT NOT NULL,
                                             status VARCHAR NOT NULL,
                                             create_dttm TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS order_to_dish (
                                             kitchen_order_id BIGINT NOT NULL,
                                             dish_id BIGINT NOT NULL,
                                             dishes_number BIGINT NOT NULL,
                                             PRIMARY KEY (kitchen_order_id, dish_id),
                                             FOREIGN KEY (kitchen_order_id)
                                                 REFERENCES kitchen_order(kitchen_order_id),
                                             FOREIGN KEY (dish_id)
                                                 REFERENCES dish(dish_id)
);

-- Вставка тестовых данных в таблицу dish
INSERT INTO dish (dish_id, balance, short_name, dish_composition)
VALUES
    (1, 10, 'test1', 'test1,test1,test1'),
    (2, 10, 'test2', 'test2,test2,test2');

