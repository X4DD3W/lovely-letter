DROP TABLE IF EXISTS game_hidden_logs;
DROP TABLE IF EXISTS game_logs;
DROP TABLE IF EXISTS game_draw_deck; -- ez majd törölhető?

DROP TABLE IF EXISTS player_cards_in_hand;
DROP TABLE IF EXISTS player_played_cards;

DROP TABLE IF EXISTS card;
DROP TABLE IF EXISTS original_card; -- ez a három is törölhető majd a lenti sémákkal?
DROP TABLE IF EXISTS new_release_card;
DROP TABLE IF EXISTS custom_card;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS game;

CREATE TABLE game
(
    game_id                      bigint auto_increment primary key,
    uuid                         varchar(255) null,
    actual_player                varchar(128),
    is_2019_version              bit(1),
    is_game_over                 bit(1),
    is_turn_of_chancellor_active bit(1),
    create_date                  timestamp default current_timestamp,
    modify_date                  timestamp default current_timestamp on update current_timestamp
);

CREATE TABLE card
(
    card_id           bigint auto_increment primary key,
    name              varchar(255) null,
    name_english      varchar(255) null,
    value             int          null,
    quantity          int          null,
    description       varchar(255) null,
    is_put_aside      bit(1),
    is_2p_public      bit(1),
    is_at_a_player    bit(1),
    game_id           bigint not null,
    KEY `game_id` (`game_id`),
    CONSTRAINT fk_card_game FOREIGN KEY (game_id) REFERENCES game (game_id)
);

CREATE TABLE player
(
    player_id         bigint auto_increment primary key,
    uuid              varchar(255) null,
    name              varchar(255) null,
    number_of_letters int          null,
    is_in_play        bit(1),
    order_number      int          null,
    game_id           bigint not null,
    KEY `game_id` (`game_id`),
    CONSTRAINT fk_player_game FOREIGN KEY (game_id) REFERENCES game (game_id)
);




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
