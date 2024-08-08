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

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

TRUNCATE TABLE user_block;
ALTER TABLE user_block ALTER COLUMN id RESTART WITH 1;

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

INSERT INTO category (name)
VALUES ('한식'),
       ('양식'),
       ('채식'),
       ('건강식'),
       ('간편식'),
       ('디저트'),
       ('해산물'),
       ('면요리'),
       ('샐러드'),
       ('스프');

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, comment_count, description, created_at)
VALUES ('김치볶음밥', 1, '00:30:00', '김치볶음밥이미지.jpg', 3, 2, 0, '김치볶음밥 조리법', '2024-07-02 13:00:00'),
       ('김밥', 1, '01:00:00', '김밥이미지.jpg', 8, 1, 0, '김밥 조리법', '2024-07-02 13:00:00'),
       ('김치찌개', 1, '00:30:00', '김치찌개이미지.jpg', 3, 2, 0, '김치찌개 조리법', '2024-07-02 13:00:00'),
       ('토마토스파게티', 1, '00:30:00', '토마토스파게티이미지.jpg', 3, 2, 0, '토마토스파게티 조리법', '2024-07-02 13:00:00'),
       ('간장계란밥', 1, '00:10:00', '간장계란밥이미지.jpg', 1, 3, 0, '간장계란밥 조리법', '2024-07-02 13:00:00'),
       ('피자', 1, '00:30:00', '피자이미지.jpg', 3, 2, 0, '피자 조리법', '2024-07-02 13:00:00'),
       ('된장찌개', 1, '00:30:00', '된장찌개이미지.jpg', 3, 2, 0, '된장찌개 조리법', '2024-07-02 13:00:00'),
       ('햄버거', 1, '00:30:00', '햄버거이미지.jpg', 3, 2, 0, '햄버거 조리법', '2024-07-02 13:00:00'),
       ('흰쌀밥', 1, '00:40:00', '흰쌀밥이미지.jpg', 2, 4, 0, '흰쌀밥 조리법', '2024-07-02 13:00:00'),
       ('샐러드', 1, '00:15:00', '샐러드이미지.jpg', 1, 5, 0, '샐러드 조리법', '2024-07-02 13:00:00'),
       ('연어스테이크', 1, '00:45:00', '연어스테이크이미지.jpg', 4, 3, 0, '연어스테이크 조리법', '2024-07-02 13:00:00'),
       ('초콜릿 케이크', 1, '01:20:00', '초콜릿케이크이미지.jpg', 5, 6, 0, '초콜릿 케이크 조리법', '2024-07-02 13:00:00'),
       ('베지터블 스프', 1, '00:50:00', '베지터블스프이미지.jpg', 3, 2, 0, '베지터블 스프 조리법', '2024-07-02 13:00:00'),
       ('카레라이스', 1, '00:30:00', '카레라이스이미지.jpg', 3, 2, 0, '카레라이스 조리법', '2024-07-02 13:00:00'),
       ('새우볶음밥', 1, '00:25:00', '새우볶음밥이미지.jpg', 2, 3, 0, '새우볶음밥 조리법', '2024-07-02 13:00:00');

INSERT INTO category_recipe (category_id, recipe_id)
VALUES (1, 2),  -- 김밥은 한식
       (3, 2),  -- 김밥은 채식
       (1, 3),  -- 김치찌개는 한식
       (5, 3),  -- 김치찌개는 간편식
       (2, 4),  -- 토마토스파게티는 양식
       (4, 5),  -- 간장계란밥은 건강식
       (5, 5),  -- 간장계란밥은 간편식
       (2, 6),  -- 피자는 양식
       (5, 6),  -- 피자는 간편식
       (1, 7),  -- 된장찌개는 한식
       (5, 7),  -- 된장찌개는 간편식
       (2, 8),  -- 햄버거는 양식
       (5, 8),  -- 햄버거는 간편식
       (1, 9),  -- 흰쌀밥은 한식
       (9, 10), -- 샐러드는 샐러드
       (4, 10), -- 샐러드는 건강식
       (7, 11), -- 연어스테이크는 해산물
       (2, 11), -- 연어스테이크는 양식
       (6, 12), -- 초콜릿 케이크는 디저트
       (10, 13),-- 베지터블 스프는 스프
       (3, 13), -- 베지터블 스프는 채식
       (1, 14), -- 카레라이스는 한식
       (5, 14), -- 카레라이스는 간편식
       (7, 15), -- 새우볶음밥은 해산물
       (1, 15); -- 새우볶음밥은 한식

INSERT INTO ingredient (name)
VALUES ('김'),
       ('쌀'),
       ('계란'),
       ('김치'),
       ('오이'),
       ('후추'),
       ('간장'),
       ('소금'),
       ('햄'),
       ('토마토'),
       ('밀가루'),
       ('새우'),
       ('버터'),
       ('설탕'),
       ('초콜릿'),
       ('버섯'),
       ('양파'),
       ('피망');

INSERT INTO ingredient_recipe (ingredient_id, recipe_id, requirement)
VALUES (1, 1, 'REQUIRED'),  -- 김치볶음밥
       (2, 1, 'REQUIRED'),  -- 김치볶음밥
       (3, 1, 'ALTERNATIVE'),  -- 김치볶음밥
       (4, 1, 'OPTIONAL'),  -- 김치볶음밥
       (2, 2, 'REQUIRED'),  -- 김밥
       (3, 2, 'OPTIONAL'),  -- 김밥
       (4, 2, 'REQUIRED'),  -- 김밥
       (2, 3, 'REQUIRED'),  -- 김치찌개
       (3, 3, 'REQUIRED'),  -- 김치찌개
       (7, 3, 'REQUIRED'),  -- 김치찌개
       (2, 4, 'REQUIRED'),  -- 토마토스파게티
       (5, 4, 'OPTIONAL'),  -- 토마토스파게티
       (10, 4, 'REQUIRED'), -- 토마토스파게티
       (8, 5, 'REQUIRED'),  -- 간장계란밥
       (3, 5, 'REQUIRED'),  -- 간장계란밥
       (9, 6, 'REQUIRED'),  -- 피자
       (6, 6, 'OPTIONAL'),  -- 피자
       (4, 7, 'REQUIRED'),  -- 된장찌개
       (2, 7, 'REQUIRED'),  -- 된장찌개
       (8, 8, 'OPTIONAL'),  -- 햄버거
       (9, 8, 'REQUIRED'),  -- 햄버거
       (2, 9, 'REQUIRED'),  -- 흰쌀밥
       (1, 9, 'OPTIONAL'),  -- 흰쌀밥
       (5, 10, 'REQUIRED'), -- 샐러드
       (8, 10, 'OPTIONAL'), -- 샐러드
       (11, 6, 'REQUIRED'), -- 피자
       (12, 15, 'REQUIRED'), -- 새우볶음밥
       (13, 15, 'OPTIONAL'), -- 새우볶음밥
       (14, 12, 'REQUIRED'), -- 초콜릿 케이크
       (15, 12, 'REQUIRED'), -- 초콜릿 케이크
       (16, 13, 'REQUIRED'), -- 베지터블 스프
       (17, 13, 'REQUIRED'), -- 베지터블 스프
       (18, 14, 'OPTIONAL'); -- 카레라이스

INSERT INTO recipe_step (recipe_id, image, description, sequence)
VALUES (1, '레시피1 설명1 이미지', '레시피1 설명1', 1),
       (1, '레시피1 설명3 이미지', '레시피1 설명3', 3),
       (1, '레시피1 설명2 이미지', '레시피1 설명2', 2);
INSERT INTO user_block (blocker_id, blockee_id)
VALUES (1, 2),
       (1, 3)
