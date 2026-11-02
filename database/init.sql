create table shortlink
(
    id         int auto_increment
        primary key,
    shortcode  varchar(30)   null,
    url        varchar(2000) null,
    creat_time datetime      null,
    constraint table_name_pk
        unique (shortcode)
);
