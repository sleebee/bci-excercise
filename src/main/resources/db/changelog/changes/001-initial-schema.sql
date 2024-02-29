CREATE TABLE phones (
    id UUID PRIMARY KEY,
    phone_number number,
    city_code short,
    country_code short
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name varchar(30),
    email varchar(100),
    password varchar(80),
    created timestamp,
    modified timestamp,
    last_login timestamp,
    token varchar(256)
);

CREATE TABLE phones_by_user (
    phone_id UUID NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (phone_id) REFERENCES phones(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_unique_phone_by_user ON phones_by_user (phone_id, user_id);
