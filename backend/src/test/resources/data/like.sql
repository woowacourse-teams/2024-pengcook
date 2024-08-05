SET
REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE users;
ALTER TABLE users
    ALTER COLUMN id RESTART;

TRUNCATE TABLE recipe;
ALTER TABLE recipe
    ALTER COLUMN id RESTART;

TRUNCATE TABLE comment;
ALTER TABLE comment
    ALTER COLUMN id RESTART;

SET
REFERENTIAL_INTEGRITY TRUE;

INSERT INTO users (email, username, nickname, image, region)
VALUES ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', 'KOREA'),
       ('loki@pengcook.net', 'loki', '로키', 'loki.jpg', 'KOREA');

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, description)
VALUES ('김밥', 1, '01:00:00', '김밥이미지.jpg', 8, 1, '김밥 조리법'),
       ('김치찌개', 1, '00:30:00', '김치찌개이미지.jpg', 3, 2, '김치찌개 조리법');

INSERT INTO recipe_like (recipe_id, user_id)
VALUES (1, 1);
