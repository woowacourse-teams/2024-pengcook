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

INSERT INTO users (email, username, nickname, image, region)
VALUES ('loki@pengcook.net', 'loki', '로키', 'loki.jpg', 'KOREA'),
       ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', 'KOREA'),
       ('crocodile@pengcook.net', 'crocodile', '악어', 'crocodile.jpg', 'KOREA'),
       ('birdsheep@pengcook.net', 'birdsheep', '새양', 'birdsheep.jpg', 'KOREA'),
       ('pond@pengcook.net', 'pond', '폰드', 'pond.jpg', 'KOREA'),
       ('ato@pengcook.net', 'ato', '아토', 'ato.jpg', 'KOREA'),
       ('km@pengcook.net', 'km', '케이엠', 'km.jpg', 'KOREA'),
       ('hadi@pengcook.net', 'hadi', '하디', 'hadi.jpg', 'KOREA');

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, description)
VALUES ('김밥', 1, '01:00:00', '김밥이미지.jpg', 8, 1, '김밥 조리법'),
       ('김치볶음밥', 2, '00:30:00', '김치볶음밥이미지.jpg', 3, 2, '김치볶음밥 조리법'),
       ('간장계란밥', 3, '00:10:00', '간장계란밥이미지.jpg', 1, 3, '간장계란밥 조리법'),
       ('흰쌀밥', 1, '00:40:00', '흰쌀밥이미지.jpg', 2, 4, '흰쌀밥 조리법');
