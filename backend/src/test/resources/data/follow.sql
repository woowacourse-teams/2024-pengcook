INSERT INTO user_follow (follower_id, followee_id)
VALUES (1, 4);

INSERT INTO recipe (title, author_id, cooking_time, thumbnail, difficulty, like_count, comment_count, description,
                    created_at)
VALUES ('치킨', 4, '01:00:00', '치킨이미지.jpg', 4, 6, 0, '치킨 조리법', '2024-12-27 15:00:00'),
       ('파스타', 4, '00:45:00', '파스타이미지.jpg', 3, 3, 0, '파스타 조리법', '2024-12-28 14:00:00'),
       ('피자', 4, '00:30:00', '피자이미지.jpg', 3, 2, 0, '피자 조리법', '2024-12-29 13:00:00'),
       ('불고기', 3, '00:30:00', '불고기이미지.jpg', 2, 5, 0, '불고기 조리법', '2024-12-30 13:00:00'),
       ('떡볶이', 3, '00:25:00', '떡볶이이미지.jpg', 3, 4, 0, '떡볶이 조리법', '2024-12-31 12:00:00');

INSERT INTO category_recipe (category_id, recipe_id)
VALUES (2, 16),
       (2, 17),
       (2, 18),
       (1, 19),
       (1, 20);
