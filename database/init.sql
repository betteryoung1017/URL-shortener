create table short_code_database.shortlink
(
    id          int auto_increment
        primary key,
    shortcode   varchar(30)   null,
    url         varchar(2000) null,
    create_time datetime      null,
    summary     varchar(255)  null,
    constraint table_name_pk
        unique (shortcode)
);

