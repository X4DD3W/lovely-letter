DROP TABLE IF EXISTS game_log;
DROP TABLE IF EXISTS games_draw_deck;

DROP TABLE IF EXISTS players_cards_in_hand;
DROP TABLE IF EXISTS players_played_cards;

DROP TABLE IF EXISTS cards_game;
DROP TABLE IF EXISTS cards_in_played_area;
DROP TABLE IF EXISTS cards_in_players_hand;

DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS original_cards;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS games;

CREATE TABLE original_cards
(
    id          bigint auto_increment primary key,
    card_name   varchar(255) null,
    card_value  int null,
    quantity    int null,
    description varchar(255) null,
    is_put_aside bit(1),
    is_at_a_player bit(1)
);

CREATE TABLE cards
(
    id          bigint auto_increment primary key,
    card_name   varchar(255) null,
    card_value  int null,
    quantity    int null,
    description varchar(255) null,
    is_put_aside bit(1),
    is_at_a_player bit(1)
);

CREATE TABLE players
(
    id      bigint auto_increment primary key,
    uuid    varchar(255) null,
    name    varchar(255) null,
    number_of_letters int null,
    is_in_play bit(1),
    order_number int null
);

CREATE TABLE games
(
    id      bigint auto_increment primary key,
    uuid    varchar(255) null
);
