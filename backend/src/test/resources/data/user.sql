DELETE
FROM user;

ALTER TABLE user
    AUTO_INCREMENT = 1;

INSERT INTO user (email, username, nickname, image, birth, gender, region)
VALUES ('loki@loki.com', 'loki', '로키', 'loki.jpg', '1999-08-08', 'MALE', 'KOREA'),
       ('ela@ela.com', 'ela', '엘라', 'ela.jpg', '2024-07-22', 'FEMALE', 'KOREA'),
       ('crocodile@crocodile.com', 'crocodile', '악어', 'crocodile.jpg', '2024-07-22', 'MALE', 'KOREA'),
       ('birdsheep@birdsheep.com', 'birdsheep', '새양', 'birdsheep.jpg', '2024-07-22', 'MALE', 'KOREA'),
       ('pond@pond.com', 'pond', '폰드', 'pond.jpg', '2024-07-22', 'MALE', 'KOREA'),
       ('ato@ato.com', 'ato', '아토', 'ato.jpg', '2024-07-22', 'FEMALE', 'KOREA'),
       ('km@km.com', 'km', '케이엠', 'km.jpg', '2024-07-22', 'MALE', 'KOREA'),
       ('hadi@hadi.com', 'hadi', '하디', 'hadi.jpg', '2024-07-22', 'MALE', 'KOREA');

