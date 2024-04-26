DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;
DROP TYPE status;

CREATE TABLE IF NOT EXISTS users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(64) NOT NULL,
    email varchar(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(64)           NOT NULL,
    description VARCHAR,
    available   BOOLEAN DEFAULT FALSE NOT NULL,
    user_id     INTEGER               NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TYPE status AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS bookings
(
    id         SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL,
    item_id    INTEGER   NOT NULL,
    user_id    INTEGER   NOT NULL,
    status     STATUS    NOT NULL DEFAULT 'WAITING',
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        SERIAL PRIMARY KEY,
    text      VARCHAR   NOT NULL,
    item_id   INTEGER   NOT NULL,
    author_id INTEGER   NOT NULL,
    cdate     TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    user_id     INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);