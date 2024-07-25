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

TRUNCATE TABLE recipe_step;
ALTER TABLE recipe_step ALTER COLUMN id RESTART;

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

INSERT INTO category (name)
VALUES ('한식'),
       ('양식'),
       ('채식'),
       ('건강식'),
       ('간편식');

INSERT INTO ingredient (name)
VALUES ('김'),
       ('쌀'),
       ('계란'),
       ('김치'),
       ('오이'),
       ('후추'),
       ('간장'),
       ('소금'),
       ('햄');

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, description)
VALUES ('김밥', 1, '01:00:00', '김밥이미지.jpg', 8, 1, '김밥 조리법'),
       ('김치볶음밥', 2, '00:30:00', '김치볶음밥이미지.jpg', 3, 2, '김치볶음밥 조리법'),
       ('간장계란밥', 3, '00:10:00', '간장계란밥이미지.jpg', 1, 3, '간장계란밥 조리법'),
       ('흰쌀밥', 1, '00:40:00', '흰쌀밥이미지.jpg', 2, 4, '흰쌀밥 조리법');

INSERT INTO category_recipe (category_id, recipe_id)
VALUES (1, 1),
       (4, 1),
       (1, 2),
       (3, 2),
       (1, 3),
       (5, 3),
       (1, 4),
       (3, 4);

INSERT INTO ingredient_recipe (ingredient_id, recipe_id, requirement)
VALUES (1, 1, 'REQUIRED'),
       (2, 1, 'REQUIRED'),
       (3, 1, 'ALTERNATIVE'),
       (4, 1, 'OPTIONAL'),
       (2, 2, 'REQUIRED'),
       (3, 2, 'OPTIONAL'),
       (4, 2, 'REQUIRED'),
       (2, 3, 'REQUIRED'),
       (3, 3, 'REQUIRED'),
       (7, 3, 'REQUIRED'),
       (2, 4, 'REQUIRED');

INSERT INTO recipe_step (recipe_id, image, description, sequence)
VALUES (1, '레시피1 설명1 이미지', '레시피1 설명1', 1),
       (1, '레시피1 설명3 이미지', '레시피1 설명3', 3),
       (1, '레시피1 설명2 이미지', '레시피1 설명2', 2);
