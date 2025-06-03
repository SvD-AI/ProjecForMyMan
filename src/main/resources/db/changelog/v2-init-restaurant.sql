CREATE TABLE restaurant (
    id              BIGSERIAL NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP,
    name            VARCHAR(255) NOT NULL,
    city            VARCHAR(255) NOT NULL,
    rating          DECIMAL(3,1),
    PRIMARY KEY (id)
);

CREATE INDEX idx_name_restaurant ON restaurant(name);

CREATE INDEX idx_city_restaurant ON restaurant(city);
