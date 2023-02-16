--liquibase formatted sql
--changeset igor:2

create index user_data_last_name_idx on user_data (last_name text_pattern_ops);

--rollback drop index user_data_last_name_idx;