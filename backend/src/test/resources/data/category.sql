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
VALUES ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', '2024-07-22', 'KOREA');

INSERT INTO category (name)
VALUES ('한식'),
       ('양식'),
       ('채식'),
       ('건강식'),
       ('간편식');

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, description)
VALUES ('김치볶음밥', 1, '00:30:00', '김치볶음밥이미지.jpg', 3, 2, '김치볶음밥 조리법');
