DROP TABLE IF exists game_log;
DROP TABLE IF exists games_draw_deck;
DROP TABLE IF exists games_players_in_game;
DROP TABLE IF exists games_players_out_of_game;
DROP TABLE IF exists games_put_aside_cards;
DROP TABLE IF exists players_cards_in_hand;
DROP TABLE IF exists players_played_cards;
DROP TABLE IF exists games;
DROP TABLE IF exists cards;
DROP TABLE IF exists players;

CREATE TABLE cards
(
    id          bigint auto_increment primary key,
    card_name   varchar(255) null,
    card_value  int null,
    quantity    int null,
    description varchar(255) null
);

CREATE TABLE players
(
    id      bigint auto_increment primary key,
    uuid    varchar(255) null,
    name    varchar(255) null,
    number_of_letters int null
);

CREATE TABLE games
(
    id      bigint auto_increment primary key,
    uuid    varchar(255) null
);
