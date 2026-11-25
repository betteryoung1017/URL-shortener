CREATE TABLE IF NOT EXISTS shortlink
(
    id          INT AUTO_INCREMENT
        PRIMARY KEY,
    shortcode   VARCHAR(30)   NULL,
    url         VARCHAR(2000) NULL,
    create_time DATETIME      NULL,
    summary     VARCHAR(255)  NULL,
    CONSTRAINT uk_shortcode
        UNIQUE (shortcode)
);
