create table "contests"
(
    id           bigserial not null
        primary key,
    contest_id   bigint,
    contest_name varchar(255),
    rank         integer,
    old_rating   integer,
    new_rating   integer,
    rating_update_time timestamp(6),
    user_id      bigint
        references users (id)
);