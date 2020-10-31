CREATE TABLE IF NOT EXISTS ranks
(
    level        smallint PRIMARY KEY,
    display_name varchar(50) UNIQUE NOT NULL,
    chat_format  varchar(150)       NOT NULL
);

--Make trigger
INSERT INTO ranks
VALUES (10, 'Player', 'Player:')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS players
(
    id       serial PRIMARY KEY,
    nick     varchar(17) UNIQUE  NOT NULL,
    password varchar             NOT NULL,
    email    varchar(75) UNIQUE,
    rank     smallint DEFAULT 10 NOT NULL REFERENCES ranks ON UPDATE SET DEFAULT
);

--Make trigger
INSERT INTO players
VALUES (1, 'unknown.', '12345', NULL, 10)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS servers
(
    id           smallserial PRIMARY KEY,
    bungee_name  varchar(30) UNIQUE NOT NULL,
    display_name varchar(30) UNIQUE NOT NULL
);

--Make triggers
INSERT INTO servers
VALUES (1, 'bungeecord', 'Sieć'),
       (2, '', 'Strona')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS active_ranks
(
    player     int REFERENCES players ON DELETE CASCADE,
    server     smallint REFERENCES servers ON DELETE CASCADE,
    rank       smallint REFERENCES ranks ON DELETE CASCADE,
    start      int NOT NULL,
    expiration int NOT NULL,
    PRIMARY KEY (player, server, rank)
);

CREATE TABLE IF NOT EXISTS permissions
(
    id         serial PRIMARY KEY,
    server     smallint    NOT NULL REFERENCES servers ON DELETE CASCADE,
    rank       smallint    NOT NULL REFERENCES ranks ON DELETE CASCADE,
    permission varchar(75) NOT NULL
);

CREATE TABLE IF NOT EXISTS ip_v4_addresses
(
    id      serial PRIMARY KEY,
    address varchar(15) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS bans
(
    id         serial PRIMARY KEY,
    server     smallint      NOT NULL REFERENCES servers ON DELETE CASCADE,
    recipient  int           NOT NULL REFERENCES players ON DELETE CASCADE,
    giver      int DEFAULT 1 NOT NULL REFERENCES players ON DELETE SET DEFAULT,
    reason     varchar(300)  NOT NULL,
    start      int           NOT NULL,
    expiration int
);

CREATE TABLE IF NOT EXISTS ip_bans
(
    ip_v4_address int PRIMARY KEY REFERENCES ip_v4_addresses ON DELETE CASCADE,
    giver         int DEFAULT 1 NOT NULL REFERENCES players ON DELETE SET DEFAULT,
    start         int           NOT NULL,
    reason        varchar(300)  NOT NULL
);

CREATE TABLE IF NOT EXISTS bans_history
(
    id                int PRIMARY KEY,
    server            smallint      NOT NULL REFERENCES servers ON DELETE CASCADE,
    recipient         int           NOT NULL REFERENCES players ON DELETE CASCADE,
    giver             int DEFAULT 1 NOT NULL REFERENCES players ON DELETE SET DEFAULT,
    ban_reason        varchar(300)  NOT NULL,
    start             int           NOT NULL,
    target_expiration int,

    real_expiration   int           NOT NULL,
    expiration_type   smallint      NOT NULL,
    expiration_reason varchar(300),
    modder            int           REFERENCES players ON DELETE SET NULL,
    new_ban           int
);

CREATE TABLE IF NOT EXISTS ip_bans_history
(
    id                serial PRIMARY KEY,
    ip_v4_address     int           NOT NULL REFERENCES ip_v4_addresses ON DELETE CASCADE,
    giver             int DEFAULT 1 NOT NULL REFERENCES players ON DELETE SET DEFAULT,
    reason            varchar(300)  NOT NULL,
    start             int           NOT NULL,

    expiration        int           NOT NULL,
    expiration_reason varchar(300),
    modder            int           REFERENCES players ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS activity_history
(
    id            serial PRIMARY KEY,
    player        int      NOT NULL REFERENCES players ON DELETE CASCADE,
    time          int      NOT NULL,
    status        smallint NOT NULL,
    ip_v4_address int      NOT NULL REFERENCES ip_v4_addresses ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ip_blockades
(
    id            serial PRIMARY KEY,
    player        int                   NOT NULL REFERENCES players ON DELETE CASCADE,
    time          int                   NOT NULL,
    ip_v4_address int                   NOT NULL REFERENCES ip_v4_addresses ON DELETE CASCADE,
    to_check      boolean DEFAULT false NOT NULL
);