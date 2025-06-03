DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'menu_items') THEN
        CREATE TABLE menu_items (
            id              BIGSERIAL NOT NULL,
            created_at      TIMESTAMP NOT NULL,
            updated_at      TIMESTAMP,
            restaurant_id   BIGINT NOT NULL,
            name            VARCHAR(255) NOT NULL,
            price           NUMERIC(10,2) NOT NULL,
            PRIMARY KEY (id),
            CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
        );
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'delivery_orders') THEN
        CREATE TABLE delivery_orders (
            id                  BIGSERIAL NOT NULL,
            created_at          TIMESTAMP NOT NULL,
            updated_at          TIMESTAMP,
            customer_name       VARCHAR(255) NOT NULL,
            address             VARCHAR(255) NOT NULL,
            restaurant_id       BIGINT NOT NULL,
            courier_id          BIGINT,
            total_price         NUMERIC(10,2) NOT NULL,
            PRIMARY KEY (id),
            CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
            CONSTRAINT fk_orders_courier FOREIGN KEY (courier_id) REFERENCES users(id) ON DELETE SET NULL
        );
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'order_items') THEN
        CREATE TABLE order_items (
            order_id            BIGINT NOT NULL,
            menu_item_id        BIGINT NOT NULL,
            quantity            INT NOT NULL,
            PRIMARY KEY (order_id, menu_item_id),
            CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES delivery_orders(id) ON DELETE CASCADE,
            CONSTRAINT fk_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
        );
    END IF;
END
$$;

CREATE INDEX idx_menu_items_restaurant_id ON menu_items(restaurant_id);
CREATE INDEX idx_orders_restaurant_id ON delivery_orders(restaurant_id);
CREATE INDEX idx_orders_courier_id ON delivery_orders(courier_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_menu_item_id ON order_items(menu_item_id);
