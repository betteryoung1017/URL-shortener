create table shortlink
(
    id         int auto_increment
        primary key,
    shortcode  varchar(30)   null,
    url        varchar(2000) null,
    create_time datetime      null,
    constraint uk_shortcode
        unique (shortcode)
);
