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

INSERT INTO dish (dish_id, balance, short_name, dish_composition)
VALUES
    (1, 100, 'Паста', 'Макароны, соус, сыр'),
    (2, 80, 'Салат', 'Овощи, масло, зелень');


INSERT INTO kitchen_order (kitchen_order_id, waiter_order_no, status, create_dttm)
VALUES
    (1, 1001, 'SENT_TO_KITCHEN', NOW()),
    (2, 1002, 'SENT_TO_KITCHEN', NOW());

INSERT INTO order_to_dish (kitchen_order_id, dish_id, dishes_number)
VALUES
    (1, 1, 2),
    (1, 2, 1),
    (2, 1, 1),
    (2, 2, 3);


INSERT INTO kitchen_order (kitchen_order_id, waiter_order_no, status, create_dttm)
VALUES
    (3, 1003, 'COOKING', NOW()),
    (4, 1004, 'COOKING', NOW());

INSERT INTO order_to_dish (kitchen_order_id, dish_id, dishes_number)
VALUES
    (3, 1, 3),
    (3, 2, 2),
    (4, 1, 1),
    (4, 2, 1);