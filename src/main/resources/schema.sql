DROP TABLE IF EXISTS game_log;
DROP TABLE IF EXISTS game_hidden_log;
DROP TABLE IF EXISTS game_draw_deck;

DROP TABLE IF EXISTS player_cards_in_hand;
DROP TABLE IF EXISTS player_played_cards;

DROP TABLE IF EXISTS card_game;
DROP TABLE IF EXISTS card_in_played_area;
DROP TABLE IF EXISTS card_in_players_hand;

DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS original_card;
DROP TABLE IF EXISTS new_release_card;
DROP TABLE IF EXISTS custom_card;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS game;

CREATE TABLE original_card
(
    id                bigint auto_increment primary key,
    card_name         varchar(255) null,
    card_name_english varchar(255) null,
    card_value        int          null,
    quantity          int          null,
    description       varchar(255) null,
    is_put_aside      bit(1),
    is_2p_public      bit(1),
    is_at_a_player    bit(1)
);

CREATE TABLE card
(
    id                bigint auto_increment primary key,
    card_name         varchar(255) null,
    card_name_english varchar(255) null,
    card_value        int          null,
    quantity          int          null,
    description       varchar(255) null,
    is_put_aside      bit(1),
    is_2p_public      bit(1),
    is_at_a_player    bit(1)
);

CREATE TABLE new_release_card
(
    id                bigint auto_increment primary key,
    card_name         varchar(255) null,
    card_name_english varchar(255) null,
    card_value        int          null,
    quantity          int          null,
    description       varchar(255) null,
    is_put_aside      bit(1),
    is_2p_public      bit(1),
    is_at_a_player    bit(1)
);

CREATE TABLE custom_card
(
    id                bigint auto_increment primary key,
    card_name         varchar(255) null,
    card_name_english varchar(255) null,
    card_value        int          null,
    quantity          int          null,
    description       varchar(255) null,
    is_put_aside      bit(1),
    is_2p_public      bit(1),
    is_at_a_player    bit(1)
);

CREATE TABLE player
(
    id                bigint auto_increment primary key,
    uuid              varchar(255) null,
    name              varchar(255) null,
    number_of_letters int          null,
    is_in_play        bit(1),
    order_number      int          null
);

CREATE TABLE game
(
    id                           bigint auto_increment primary key,
    uuid                         varchar(255) null,
    actual_player                varchar(128),
    is_2019_version              bit(1),
    is_game_over                 bit(1),
    is_turn_of_chancellor_active bit(1),
    create_date                  timestamp default current_timestamp,
    modify_date                  timestamp default current_timestamp on update current_timestamp
);
