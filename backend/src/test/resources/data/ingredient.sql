SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE users;
ALTER TABLE users ALTER COLUMN id RESTART;

TRUNCATE TABLE category;
ALTER TABLE category ALTER COLUMN id RESTART;

TRUNCATE TABLE ingredient;
ALTER TABLE ingredient ALTER COLUMN id RESTART;

TRUNCATE TABLE recipe;
ALTER TABLE recipe ALTER COLUMN id RESTART;

TRUNCATE TABLE category_recipe;
ALTER TABLE category_recipe ALTER COLUMN id RESTART;

TRUNCATE TABLE ingredient_recipe;
ALTER TABLE ingredient_recipe ALTER COLUMN id RESTART;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO users (email, username, nickname, image, birth, region)
VALUES ('loki@pengcook.net', 'loki', '로키', 'loki.jpg', '1999-08-08', 'KOREA'),
       ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', '2024-07-22', 'KOREA'),
       ('crocodile@pengcook.net', 'crocodile', '악어', 'crocodile.jpg', '2024-07-22', 'KOREA'),
       ('birdsheep@pengcook.net', 'birdsheep', '새양', 'birdsheep.jpg', '2024-07-22', 'KOREA'),
       ('pond@pengcook.net', 'pond', '폰드', 'pond.jpg', '2024-07-22', 'KOREA'),
       ('ato@pengcook.net', 'ato', '아토', 'ato.jpg', '2024-07-22', 'KOREA'),
       ('km@pengcook.net', 'km', '케이엠', 'km.jpg', '2024-07-22', 'KOREA'),
       ('hadi@pengcook.net', 'hadi', '하디', 'hadi.jpg', '2024-07-22', 'KOREA');

