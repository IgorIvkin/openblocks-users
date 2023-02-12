--liquibase formatted sql
--changeset igor:1

create table user_data
(
    id              bigserial                not null,
    user_name       character varying(255)   not null,
    first_name      character varying(255)   not null,
    last_name       character varying(255)   not null,
    patronymic_name character varying(255)   null,
    birth_date      date                     null,
    created_at      timestamp with time zone not null default NOW(),
    constraint user_data_pkey primary key (id)
);

create unique index user_data_user_name_idx ON user_data (user_name);

--rollback DROP TABLE user;