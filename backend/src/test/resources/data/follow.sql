SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE users;
ALTER TABLE users
    ALTER COLUMN id RESTART;

TRUNCATE TABLE follow;
ALTER TABLE follow
    ALTER COLUMN id RESTART;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO users (email, username, nickname, image, region)
VALUES ('ela@pengcook.net', 'ela', '엘라', 'ela.jpg', 'KOREA'),
       ('loki@pengcook.net', 'loki', '로키', 'loki.jpg', 'KOREA'),
       ('seyang@pengcook.net', 'seyang', '새양', 'seyang.jpg', 'GERMANY');

INSERT INTO follow (followee_id, follower_id)
VALUES (2, 1);
