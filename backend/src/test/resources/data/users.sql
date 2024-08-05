SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE user_report;
ALTER TABLE user_report ALTER COLUMN id RESTART WITH 1;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO users (email, username, nickname, image, region)
VALUES ('loki@pengcook.net', 'loki', '로키', 'loki.jpg', 'KOREA'),
       ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', 'KOREA'),
       ('crocodile@pengcook.net', 'crocodile', '악어', 'crocodile.jpg', 'KOREA'),
       ('birdsheep@pengcook.net', 'birdsheep', '새양', 'birdsheep.jpg', 'KOREA'),
       ('pond@pengcook.net', 'pond', '폰드', 'pond.jpg', 'KOREA'),
       ('ato@pengcook.net', 'ato', '아토', 'ato.jpg', 'KOREA'),
       ('km@pengcook.net', 'km', '케이엠', 'km.jpg', 'KOREA'),
       ('hadi@pengcook.net', 'hadi', '하디', 'hadi.jpg', 'KOREA');

