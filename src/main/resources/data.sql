
-- 먼저 members와 nail_artists 테이블에 데이터 삽입
INSERT INTO members (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Member', 'member@example.com', 'MEMBER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'member@example.com');

INSERT INTO members (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Member', CONCAT('member', n, '@example.com'), 'MEMBER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO',
       CONCAT('3588226794', n), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (SELECT 2 AS n UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11) AS nums
WHERE NOT EXISTS (SELECT 2 FROM members WHERE email = CONCAT('member', nums.n, '@example.com'));


INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Manager', 'manager@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'manager@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '모비쌤', 'mobi1@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 2 FROM nail_artists WHERE email = 'mobi1@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '비모쌤', 'mobi2@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 3 FROM nail_artists WHERE email = 'mobi2@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '피넛쌤', 'mobi3@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 4 FROM nail_artists WHERE email = 'mobi3@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '케이쌤', 'mobi4@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 5 FROM nail_artists WHERE email = 'mobi4@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '제로쌤', 'mobi5@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 6 FROM nail_artists WHERE email = 'mobi5@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '조이쌤', 'mobi6@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 7 FROM nail_artists WHERE email = 'mobi6@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '제인쌤', 'mobi7@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 8 FROM nail_artists WHERE email = 'mobi7@example.com');

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '알루미늄쌤', 'mobi8@example.com', 'MANAGER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 9 FROM nail_artists WHERE email = 'mobi8@example.com');

-- 모비네일 강남점 데이터 삽입
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '모비네일 강남점', '01012341234', 8, '서울 강남구 봉은사로6길 29 1층 102호', '매달 네일 오마카세를 제공하는 디자인 맛집 모비네일 \n현재 당일 예약 가능합니다', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'manager@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '모비네일 강남점');

-- 모비네일 강남점 아티스트 
UPDATE nail_artists
SET shop_id=1
WHERE shop_id IS NULL;

-- 나의네일 데이터 삽입
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '나의네일', '01087872938', 8, '경기 광주시 태재로 102 대진프라자 108호', '매달 네일 오마카세를 제공하는 디자인 맛집 나의네일 \n현재 당일 예약 가능합니다', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'mobi3@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '나의네일');

-- 유네일 데이터 삽입
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '유네일', '01087872938', 8, '서울 동작구 장승배기로10길 100', '매달 네일 오마카세를 제공하는 디자인 맛집 유네일 \n현재 당일 예약 가능합니다', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'mobi4@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '유네일');

-- shop_info 테이블에 데이터 삽입
INSERT INTO shop_info (shop_id, created_at, modified_at)
SELECT s.shop_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM shops s WHERE s.shop_name = '모비네일 강남점' AND NOT EXISTS (SELECT 1 FROM shop_info si WHERE si.shop_id = s.shop_id);

-- 월요일부터 일요일까지의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT * FROM (
                  VALUES
                      (1, 0, true, '09:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 1, true, '09:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 2, true, '09:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 3, true, '09:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 4, true, '09:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 5, true, '10:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                      (1, 6, true, '10:00:00'::time, '22:00:00'::time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
              ) AS temp
WHERE NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = 1 AND day_of_week IN (0, 1, 2, 3, 4, 5, 6)
);

-- 예약(reservations) 데이터 삽입
INSERT INTO reservations (shop_id, member_id, created_at, modified_at)
SELECT * FROM (
                  VALUES
(1, 1, '2024-06-27 11:00:00'::timestamp, '2024-06-27 11:00:00'::timestamp),
(1, 1, '2024-06-27 12:00:00'::timestamp, '2024-06-27 12:00:00'::timestamp),
(1, 1, '2024-06-27 15:00:00'::timestamp, '2024-06-27 15:00:00'::timestamp),
(1, 1, '2024-06-27 15:00:00'::timestamp, '2024-06-27 15:00:00'::timestamp),
(1, 1, '2024-06-27 16:00:00'::timestamp, '2024-06-27 16:00:00'::timestamp),
(1, 1, '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp),
(1, 1, '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp),
(1, 1, '2024-06-27 19:00:00'::timestamp, '2024-06-27 19:00:00'::timestamp),
(1, 1, '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
(1, 1, '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
(1, 1, '2024-06-28 22:00:00'::timestamp, '2024-06-28 22:00:00'::timestamp),
(1, 1, '2024-06-29 20:00:00'::timestamp, '2024-06-29 20:00:00'::timestamp),
(1, 1, '2024-06-30 10:00:00'::timestamp, '2024-06-30 10:00:00'::timestamp),
(1, 1, '2024-06-30 11:00:00'::timestamp, '2024-06-30 11:00:00'::timestamp),
(1, 1, '2024-06-30 12:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp),
(1, 1, '2024-06-30 13:00:00'::timestamp, '2024-06-30 13:00:00'::timestamp),
(1, 1, '2024-06-30 14:00:00'::timestamp, '2024-06-30 14:00:00'::timestamp),
(1, 1, '2024-06-30 15:00:00'::timestamp, '2024-06-30 15:00:00'::timestamp),
(1, 1, '2024-06-30 16:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp),
(1, 1, '2024-06-30 17:00:00'::timestamp, '2024-06-30 17:00:00'::timestamp),
(1, 1, '2024-06-30 18:00:00'::timestamp, '2024-06-30 18:00:00'::timestamp),
(1, 1, '2024-06-30 19:00:00'::timestamp, '2024-06-30 19:00:00'::timestamp),
(1, 1, '2024-07-01 10:00:00'::timestamp, '2024-07-01 10:00:00'::timestamp),
(1, 1, '2024-07-01 11:00:00'::timestamp, '2024-07-01 11:00:00'::timestamp),
(1, 1, '2024-07-01 12:00:00'::timestamp, '2024-07-01 12:00:00'::timestamp),
(1, 1, '2024-07-01 13:00:00'::timestamp, '2024-07-01 13:00:00'::timestamp),
(1, 1, '2024-07-01 14:00:00'::timestamp, '2024-07-01 14:00:00'::timestamp),
(1, 1, '2024-07-01 15:00:00'::timestamp, '2024-07-01 15:00:00'::timestamp),
(1, 1, '2024-07-01 16:00:00'::timestamp, '2024-07-01 16:00:00'::timestamp),
(1, 1, '2024-07-01 17:00:00'::timestamp, '2024-07-01 17:00:00'::timestamp),
(1, 1, '2024-07-01 18:00:00'::timestamp, '2024-07-01 18:00:00'::timestamp),
(1, 1, '2024-07-01 19:00:00'::timestamp, '2024-07-01 19:00:00'::timestamp),
(1, 1, '2024-07-02 10:00:00'::timestamp, '2024-07-02 10:00:00'::timestamp),
(1, 1, '2024-07-02 11:00:00'::timestamp, '2024-07-02 11:00:00'::timestamp),
(1, 1, '2024-07-02 12:00:00'::timestamp, '2024-07-02 12:00:00'::timestamp),
(1, 1, '2024-07-02 13:00:00'::timestamp, '2024-07-02 13:00:00'::timestamp),
(1, 1, '2024-07-02 14:00:00'::timestamp, '2024-07-02 14:00:00'::timestamp),
(1, 1, '2024-07-02 15:00:00'::timestamp, '2024-07-02 15:00:00'::timestamp),
(1, 1, '2024-07-02 16:00:00'::timestamp, '2024-07-02 16:00:00'::timestamp),
(1, 1, '2024-07-02 17:00:00'::timestamp, '2024-07-02 17:00:00'::timestamp),
(1, 1, '2024-07-02 18:00:00'::timestamp, '2024-07-02 18:00:00'::timestamp),
(1, 1, '2024-07-02 19:00:00'::timestamp, '2024-07-02 19:00:00'::timestamp),
(1, 1, '2024-07-03 10:00:00'::timestamp, '2024-07-03 10:00:00'::timestamp),
(1, 1, '2024-07-03 11:00:00'::timestamp, '2024-07-03 11:00:00'::timestamp),
(1, 1, '2024-07-03 12:00:00'::timestamp, '2024-07-03 12:00:00'::timestamp),
(1, 1, '2024-07-03 13:00:00'::timestamp, '2024-07-03 13:00:00'::timestamp),
(1, 1, '2024-07-03 14:00:00'::timestamp, '2024-07-03 14:00:00'::timestamp),
(1, 1, '2024-07-03 15:00:00'::timestamp, '2024-07-03 15:00:00'::timestamp),
(1, 1, '2024-07-03 16:00:00'::timestamp, '2024-07-03 16:00:00'::timestamp),
(1, 1, '2024-07-03 17:00:00'::timestamp, '2024-07-03 17:00:00'::timestamp),
(1, 1, '2024-07-03 18:00:00'::timestamp, '2024-07-03 18:00:00'::timestamp),
(1, 1, '2024-07-03 19:00:00'::timestamp, '2024-07-03 19:00:00'::timestamp),
(1, 1, '2024-07-04 10:00:00'::timestamp, '2024-07-04 10:00:00'::timestamp),
(1, 1, '2024-07-04 11:00:00'::timestamp, '2024-07-04 11:00:00'::timestamp),
(1, 1, '2024-07-04 12:00:00'::timestamp, '2024-07-04 12:00:00'::timestamp),
(1, 1, '2024-07-04 13:00:00'::timestamp, '2024-07-04 13:00:00'::timestamp),
(1, 1, '2024-07-04 14:00:00'::timestamp, '2024-07-04 14:00:00'::timestamp),
(1, 1, '2024-07-04 15:00:00'::timestamp, '2024-07-04 15:00:00'::timestamp),
(1, 1, '2024-07-04 16:00:00'::timestamp, '2024-07-04 16:00:00'::timestamp),
(1, 1, '2024-07-04 17:00:00'::timestamp, '2024-07-04 17:00:00'::timestamp),
(1, 1, '2024-07-04 18:00:00'::timestamp, '2024-07-04 18:00:00'::timestamp),
(1, 1, '2024-07-04 19:00:00'::timestamp, '2024-07-04 19:00:00'::timestamp),
(1, 1, '2024-07-05 10:00:00'::timestamp, '2024-07-05 10:00:00'::timestamp),
(1, 1, '2024-07-05 11:00:00'::timestamp, '2024-07-05 11:00:00'::timestamp),
(1, 1, '2024-07-05 12:00:00'::timestamp, '2024-07-05 12:00:00'::timestamp),
(1, 1, '2024-07-05 13:00:00'::timestamp, '2024-07-05 13:00:00'::timestamp),
(1, 1, '2024-07-05 14:00:00'::timestamp, '2024-07-05 14:00:00'::timestamp),
(1, 1, '2024-07-05 15:00:00'::timestamp, '2024-07-05 15:00:00'::timestamp),
(1, 1, '2024-07-05 16:00:00'::timestamp, '2024-07-05 16:00:00'::timestamp),
(1, 1, '2024-07-05 17:00:00'::timestamp, '2024-07-05 17:00:00'::timestamp),
(1, 1, '2024-07-05 18:00:00'::timestamp, '2024-07-05 18:00:00'::timestamp),
(1, 1, '2024-07-05 19:00:00'::timestamp, '2024-07-05 19:00:00'::timestamp),
(1, 1, '2024-07-06 10:00:00'::timestamp, '2024-07-06 10:00:00'::timestamp),
(1, 1, '2024-07-06 11:00:00'::timestamp, '2024-07-06 11:00:00'::timestamp),
(1, 1, '2024-07-06 12:00:00'::timestamp, '2024-07-06 12:00:00'::timestamp),
(1, 1, '2024-07-06 13:00:00'::timestamp, '2024-07-06 13:00:00'::timestamp),
(1, 1, '2024-07-06 14:00:00'::timestamp, '2024-07-06 14:00:00'::timestamp),
(1, 1, '2024-07-06 15:00:00'::timestamp, '2024-07-06 15:00:00'::timestamp),
(1, 1, '2024-07-06 16:00:00'::timestamp, '2024-07-06 16:00:00'::timestamp),
(1, 1, '2024-07-06 17:00:00'::timestamp, '2024-07-06 17:00:00'::timestamp),
(1, 1, '2024-07-06 18:00:00'::timestamp, '2024-07-06 18:00:00'::timestamp),
(1, 1, '2024-07-06 19:00:00'::timestamp, '2024-07-06 19:00:00'::timestamp),
(1, 1, '2024-07-07 10:00:00'::timestamp, '2024-07-07 10:00:00'::timestamp),
(1, 1, '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp),
(1, 1, '2024-07-07 12:00:00'::timestamp, '2024-07-07 12:00:00'::timestamp),
(1, 1, '2024-07-07 13:00:00'::timestamp, '2024-07-07 13:00:00'::timestamp),
(1, 1, '2024-07-07 14:00:00'::timestamp, '2024-07-07 14:00:00'::timestamp),
(1, 1, '2024-07-07 15:00:00'::timestamp, '2024-07-07 15:00:00'::timestamp),
(1, 1, '2024-07-07 16:00:00'::timestamp, '2024-07-07 16:00:00'::timestamp),
(1, 1, '2024-07-07 17:00:00'::timestamp, '2024-07-07 17:00:00'::timestamp),
(1, 1, '2024-07-07 18:00:00'::timestamp, '2024-07-07 18:00:00'::timestamp),
(1, 1, '2024-07-07 19:00:00'::timestamp, '2024-07-07 19:00:00'::timestamp),
(1, 1, '2024-07-08 10:00:00'::timestamp, '2024-07-08 10:00:00'::timestamp),
(1, 1, '2024-07-08 11:00:00'::timestamp, '2024-07-08 11:00:00'::timestamp),
(1, 1, '2024-07-08 12:00:00'::timestamp, '2024-07-08 12:00:00'::timestamp),
(1, 1, '2024-07-08 13:00:00'::timestamp, '2024-07-08 13:00:00'::timestamp),
(1, 1, '2024-07-08 14:00:00'::timestamp, '2024-07-08 14:00:00'::timestamp),
(1, 1, '2024-07-08 15:00:00'::timestamp, '2024-07-08 15:00:00'::timestamp),
(1, 1, '2024-07-08 16:00:00'::timestamp, '2024-07-08 16:00:00'::timestamp),
( 1, 1, '2024-07-08 17:00:00'::timestamp, '2024-07-08 17:00:00'::timestamp),
( 1, 1, '2024-07-08 18:00:00'::timestamp, '2024-07-08 18:00:00'::timestamp),
( 1, 1, '2024-07-08 19:00:00'::timestamp, '2024-07-08 19:00:00'::timestamp),
( 1, 1, '2024-07-09 10:00:00'::timestamp, '2024-07-09 10:00:00'::timestamp),
( 1, 1, '2024-07-09 11:00:00'::timestamp, '2024-07-09 11:00:00'::timestamp),
( 1, 1, '2024-07-09 12:00:00'::timestamp, '2024-07-09 12:00:00'::timestamp),
( 1, 1, '2024-07-09 13:00:00'::timestamp, '2024-07-09 13:00:00'::timestamp),
( 1, 1, '2024-07-09 14:00:00'::timestamp, '2024-07-09 14:00:00'::timestamp),
( 1, 1, '2024-07-09 15:00:00'::timestamp, '2024-07-09 15:00:00'::timestamp),
( 1, 1, '2024-07-09 16:00:00'::timestamp, '2024-07-09 16:00:00'::timestamp),
( 1, 1, '2024-07-09 17:00:00'::timestamp, '2024-07-09 17:00:00'::timestamp),
( 1, 1, '2024-07-09 18:00:00'::timestamp, '2024-07-09 18:00:00'::timestamp),
( 1, 1, '2024-07-09 19:00:00'::timestamp, '2024-07-09 19:00:00'::timestamp),
( 1, 1, '2024-07-10 10:00:00'::timestamp, '2024-07-10 10:00:00'::timestamp),
( 1, 1, '2024-07-10 11:00:00'::timestamp, '2024-07-10 11:00:00'::timestamp),
( 1, 1, '2024-07-10 12:00:00'::timestamp, '2024-07-10 12:00:00'::timestamp),
( 1, 1, '2024-07-10 13:00:00'::timestamp, '2024-07-10 13:00:00'::timestamp),
( 1, 1, '2024-07-10 14:00:00'::timestamp, '2024-07-10 14:00:00'::timestamp),
( 1, 1, '2024-07-10 15:00:00'::timestamp, '2024-07-10 15:00:00'::timestamp),
( 1, 1, '2024-07-10 16:00:00'::timestamp, '2024-07-10 16:00:00'::timestamp),
( 1, 1, '2024-07-10 17:00:00'::timestamp, '2024-07-10 17:00:00'::timestamp) ,
( 1, 1, '2024-07-10 17:00:00'::timestamp, '2024-07-10 17:00:00'::timestamp) ) AS temp
WHERE NOT EXISTS (
    SELECT 1
    FROM reservations
    WHERE shop_id = 1
      AND created_at >= '2024-06-27 11:00:00'::timestamp
        AND created_at <= '2024-07-10 17:00:00'::timestamp
);


-- 처리 정보 삽입
INSERT INTO reservation_details (reservation_id, shop_id, status, remove, start_time, end_time, created_at, modified_at, extend, nail_artist_id)
SELECT * FROM (VALUES
(1, 1,'CONFIRMED', 'IN_SHOP', '2024-06-26 11:00:00'::timestamp, '2024-06-26 13:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp, false, 1),
(2, 1,'CONFIRMED', 'ELSE_WHERE', '2024-06-26 14:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp, false, 2),
(3, 1,'PENDING', 'ELSE_WHERE', '2024-06-27 09:00:00'::timestamp, '2024-06-27 11:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, false, null),
(4, 1,'CONFIRMED', 'NO_NEED', '2024-06-27 13:00:00'::timestamp, '2024-06-27 14:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, true, 3),
(5, 1,'CANCELED', 'NO_NEED', '2024-06-27 15:00:00'::timestamp, '2024-06-27 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, false, null),
(6, 1,'PENDING', 'IN_SHOP', '2024-06-28 10:00:00'::timestamp, '2024-06-28 11:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp, true,null),
(7, 1,'PENDING', 'IN_SHOP', '2024-06-28 14:00:00'::timestamp, '2024-06-28 15:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp, false,null),
(8, 1,'CONFIRMED', 'NO_NEED', '2024-06-29 09:00:00'::timestamp, '2024-06-29 11:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp, false, 4),
(9, 1,'CONFIRMED', 'IN_SHOP', '2024-06-29 13:00:00'::timestamp, '2024-06-29 14:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp, true, 5),
(10, 1,'REJECTED', 'NO_NEED', '2024-06-29 16:00:00'::timestamp, '2024-06-29 18:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp, false,null),
(11, 1,'CONFIRMED', 'IN_SHOP', '2024-06-30 10:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp, false, 6),
(12, 1,'CONFIRMED', 'IN_SHOP', '2024-06-30 14:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7),
(13, 1,'REJECTED', 'IN_SHOP', '2024-06-30 10:00:00'::timestamp, '2024-06-30 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false,null),
(14, 1,'PENDING', 'NO_NEED', '2024-06-30 11:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true,null),
(15, 1,'CANCELED', 'IN_SHOP', '2024-06-30 14:00:00'::timestamp, '2024-06-30 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(16, 1,'PENDING', 'IN_SHOP', '2024-07-01 10:00:00'::timestamp, '2024-07-01 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(17, 1,'CONFIRMED', 'NO_NEED', '2024-07-01 16:00:00'::timestamp, '2024-07-01 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 8),
(18, 1,'PENDING', 'ELSE_WHERE', '2024-07-01 16:00:00'::timestamp, '2024-07-01 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(19, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-02 10:00:00'::timestamp, '2024-07-02 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 9),
(20, 1,'PENDING', 'ELSE_WHERE', '2024-07-02 12:00:00'::timestamp, '2024-07-02 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(21, 1,'CONFIRMED', 'IN_SHOP', '2024-07-02 15:00:00'::timestamp, '2024-07-02 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 1),
(22, 1,'PENDING', 'NO_NEED', '2024-07-02 17:00:00'::timestamp, '2024-07-02 18:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(23, 1,'PENDING', 'IN_SHOP', '2024-07-03 10:00:00'::timestamp, '2024-07-03 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(24, 1,'CONFIRMED', 'NO_NEED', '2024-07-03 14:00:00'::timestamp, '2024-07-03 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 2),
(25, 1,'PENDING', 'ELSE_WHERE', '2024-07-04 10:00:00'::timestamp, '2024-07-04 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(26, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-04 13:00:00'::timestamp, '2024-07-04 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 3),
(27, 1,'PENDING', 'ELSE_WHERE', '2024-07-04 16:00:00'::timestamp, '2024-07-04 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(28, 1,'CONFIRMED', 'IN_SHOP', '2024-07-05 10:00:00'::timestamp, '2024-07-05 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 4),
(29, 1,'PENDING', 'NO_NEED', '2024-07-05 12:00:00'::timestamp, '2024-07-05 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(30, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-05 15:00:00'::timestamp, '2024-07-05 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 5),
(31, 1,'PENDING', 'ELSE_WHERE', '2024-07-06 11:00:00'::timestamp, '2024-07-06 12:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(32, 1,'CONFIRMED', 'IN_SHOP', '2024-07-06 14:00:00'::timestamp, '2024-07-06 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 6),
(33, 1,'PENDING', 'NO_NEED', '2024-07-07 12:00:00'::timestamp, '2024-07-07 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(34, 1,'PENDING', 'IN_SHOP', '2024-07-07 15:00:00'::timestamp, '2024-07-07 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(35, 1,'CONFIRMED', 'NO_NEED', '2024-07-07 10:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7),
(36, 1,'PENDING', 'ELSE_WHERE', '2024-07-07 14:00:00'::timestamp, '2024-07-07 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(37, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-08 10:00:00'::timestamp, '2024-07-08 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 8),
(38, 1,'PENDING', 'ELSE_WHERE', '2024-07-08 12:00:00'::timestamp, '2024-07-08 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(39, 1,'CONFIRMED', 'IN_SHOP', '2024-07-08 15:00:00'::timestamp, '2024-07-08 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 9),
(40, 1,'PENDING', 'NO_NEED', '2024-07-09 10:00:00'::timestamp, '2024-07-09 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(41, 1,'PENDING', 'IN_SHOP', '2024-07-09 13:00:00'::timestamp, '2024-07-09 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(42, 1,'CONFIRMED', 'NO_NEED', '2024-07-09 16:00:00'::timestamp, '2024-07-09 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 1),
(43, 1,'PENDING', 'ELSE_WHERE', '2024-07-10 10:00:00'::timestamp, '2024-07-10 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(44, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-10 13:00:00'::timestamp, '2024-07-10 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 2),
(45, 1,'PENDING', 'ELSE_WHERE', '2024-07-10 16:00:00'::timestamp, '2024-07-10 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(46, 1,'CONFIRMED', 'IN_SHOP', '2024-07-11 10:00:00'::timestamp, '2024-07-11 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 3),
(47, 1,'PENDING', 'NO_NEED', '2024-07-11 12:00:00'::timestamp, '2024-07-11 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(48, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-11 15:00:00'::timestamp, '2024-07-11 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 4),
(49, 1,'PENDING', 'ELSE_WHERE', '2024-07-11 17:00:00'::timestamp, '2024-07-11 18:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(50, 1,'CONFIRMED', 'IN_SHOP', '2024-07-12 10:00:00'::timestamp, '2024-07-12 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 5),
(51, 1,'PENDING', 'NO_NEED', '2024-07-12 12:00:00'::timestamp, '2024-07-12 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(52, 1,'PENDING', 'IN_SHOP', '2024-07-12 14:00:00'::timestamp, '2024-07-12 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(53, 1,'CONFIRMED', 'NO_NEED', '2024-07-12 16:00:00'::timestamp, '2024-07-12 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 6),
(54, 1,'PENDING', 'ELSE_WHERE', '2024-07-13 10:00:00'::timestamp, '2024-07-13 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(55, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-13 13:00:00'::timestamp, '2024-07-13 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7),
(56, 1,'PENDING', 'ELSE_WHERE', '2024-07-13 16:00:00'::timestamp, '2024-07-13 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(57, 1,'CONFIRMED', 'IN_SHOP', '2024-07-14 10:00:00'::timestamp, '2024-07-14 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 8),
(57, 1,'CONFIRMED', 'NO_NEED', '2024-07-14 10:00:00'::timestamp, '2024-07-14 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 8),
(58, 1,'PENDING', 'NO_NEED', '2024-07-14 12:00:00'::timestamp, '2024-07-14 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(59, 1,'PENDING', 'IN_SHOP', '2024-07-14 14:00:00'::timestamp, '2024-07-14 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(60, 1,'CONFIRMED', 'NO_NEED', '2024-07-14 16:00:00'::timestamp, '2024-07-14 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 9),
(61, 1,'PENDING', 'ELSE_WHERE', '2024-07-15 10:00:00'::timestamp, '2024-07-15 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(62, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-15 13:00:00'::timestamp, '2024-07-15 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 1),
(63, 1,'PENDING', 'ELSE_WHERE', '2024-07-15 16:00:00'::timestamp, '2024-07-15 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(64, 1,'CONFIRMED', 'IN_SHOP', '2024-07-16 10:00:00'::timestamp, '2024-07-16 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 2),
(65, 1,'PENDING', 'NO_NEED', '2024-07-16 12:00:00'::timestamp, '2024-07-16 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(66, 1,'PENDING', 'IN_SHOP', '2024-07-16 14:00:00'::timestamp, '2024-07-16 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(67, 1,'CONFIRMED', 'NO_NEED', '2024-07-16 16:00:00'::timestamp, '2024-07-16 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 3),
(68, 1,'PENDING', 'ELSE_WHERE', '2024-07-17 10:00:00'::timestamp, '2024-07-17 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(69, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-17 13:00:00'::timestamp, '2024-07-17 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 4),
(70, 1,'PENDING', 'ELSE_WHERE', '2024-07-17 16:00:00'::timestamp, '2024-07-17 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(71, 1,'CONFIRMED', 'IN_SHOP', '2024-07-18 10:00:00'::timestamp, '2024-07-18 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 5),
(72, 1,'PENDING', 'NO_NEED', '2024-07-18 12:00:00'::timestamp, '2024-07-18 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(73, 1,'PENDING', 'IN_SHOP', '2024-07-18 14:00:00'::timestamp, '2024-07-18 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(74, 1,'CONFIRMED', 'NO_NEED', '2024-07-18 16:00:00'::timestamp, '2024-07-18 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 6),
(75, 1,'PENDING', 'ELSE_WHERE', '2024-07-19 10:00:00'::timestamp, '2024-07-19 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(76, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-19 13:00:00'::timestamp, '2024-07-19 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 7),
(77, 1,'PENDING', 'ELSE_WHERE', '2024-07-19 16:00:00'::timestamp, '2024-07-19 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(78, 1,'CONFIRMED', 'IN_SHOP', '2024-07-20 10:00:00'::timestamp, '2024-07-20 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 8),
(79, 1,'PENDING', 'NO_NEED', '2024-07-20 12:00:00'::timestamp, '2024-07-20 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(80, 1,'PENDING', 'IN_SHOP', '2024-07-20 14:00:00'::timestamp, '2024-07-20 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(81, 1,'CONFIRMED', 'NO_NEED', '2024-07-20 16:00:00'::timestamp, '2024-07-20 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 9),
(82, 1,'PENDING', 'ELSE_WHERE', '2024-07-21 10:00:00'::timestamp, '2024-07-21 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(83, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-21 13:00:00'::timestamp, '2024-07-21 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 1),
(84, 1,'PENDING', 'ELSE_WHERE', '2024-07-21 16:00:00'::timestamp, '2024-07-21 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(85, 1,'CONFIRMED', 'IN_SHOP', '2024-07-22 10:00:00'::timestamp, '2024-07-22 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 2),
(86, 1,'PENDING', 'NO_NEED', '2024-07-22 12:00:00'::timestamp, '2024-07-22 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(87, 1,'PENDING', 'IN_SHOP', '2024-07-22 14:00:00'::timestamp, '2024-07-22 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(88, 1,'CONFIRMED', 'NO_NEED', '2024-07-22 16:00:00'::timestamp, '2024-07-22 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 3),
(89, 1,'PENDING', 'ELSE_WHERE', '2024-07-23 10:00:00'::timestamp, '2024-07-23 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(90, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-23 13:00:00'::timestamp, '2024-07-23 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 4),
(91, 1,'PENDING', 'ELSE_WHERE', '2024-07-23 16:00:00'::timestamp, '2024-07-23 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(92, 1,'CONFIRMED', 'IN_SHOP', '2024-07-24 10:00:00'::timestamp, '2024-07-24 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 5),
(93, 1,'PENDING', 'NO_NEED', '2024-07-24 12:00:00'::timestamp, '2024-07-24 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(94, 1,'PENDING', 'IN_SHOP', '2024-07-24 14:00:00'::timestamp, '2024-07-24 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(95, 1,'CONFIRMED', 'NO_NEED', '2024-07-24 16:00:00'::timestamp, '2024-07-24 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 6),
(96, 1,'PENDING', 'ELSE_WHERE', '2024-07-25 10:00:00'::timestamp, '2024-07-25 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(97, 1,'CONFIRMED', 'ELSE_WHERE', '2024-07-25 13:00:00'::timestamp, '2024-07-25 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7),
(98, 1,'PENDING', 'ELSE_WHERE', '2024-07-25 16:00:00'::timestamp, '2024-07-25 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(99, 1,'CONFIRMED', 'IN_SHOP', '2024-07-26 10:00:00'::timestamp, '2024-07-26 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 8),
(100, 1, 'PENDING', 'NO_NEED', '2024-07-26 12:00:00'::timestamp, '2024-07-26 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(101, 1, 'PENDING', 'IN_SHOP', '2024-07-26 14:00:00'::timestamp, '2024-07-26 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(102, 1, 'CONFIRMED', 'NO_NEED', '2024-07-26 16:00:00'::timestamp, '2024-07-26 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 9),
(103, 1, 'PENDING', 'ELSE_WHERE', '2024-07-27 10:00:00'::timestamp, '2024-07-27 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(104, 1, 'CONFIRMED', 'ELSE_WHERE', '2024-07-27 13:00:00'::timestamp, '2024-07-27 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 1),
(105, 1, 'PENDING', 'ELSE_WHERE', '2024-07-27 16:00:00'::timestamp, '2024-07-27 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(106, 1, 'CONFIRMED', 'IN_SHOP', '2024-07-28 10:00:00'::timestamp, '2024-07-28 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 2),
(107, 1, 'PENDING', 'NO_NEED', '2024-07-28 12:00:00'::timestamp, '2024-07-28 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(108, 1, 'PENDING', 'IN_SHOP', '2024-07-28 14:00:00'::timestamp, '2024-07-28 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(109, 1, 'CONFIRMED', 'NO_NEED', '2024-07-28 16:00:00'::timestamp, '2024-07-28 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 3),
(110, 1, 'PENDING', 'ELSE_WHERE', '2024-07-29 10:00:00'::timestamp, '2024-07-29 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(111, 1, 'CONFIRMED', 'ELSE_WHERE', '2024-07-29 13:00:00'::timestamp, '2024-07-29 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 3),
(112, 1, 'PENDING', 'ELSE_WHERE', '2024-07-29 16:00:00'::timestamp, '2024-07-29 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(113, 1, 'CONFIRMED', 'IN_SHOP', '2024-07-30 10:00:00'::timestamp, '2024-07-30 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 4),
(114, 1, 'PENDING', 'NO_NEED', '2024-07-30 12:00:00'::timestamp, '2024-07-30 13:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(115, 1, 'PENDING', 'IN_SHOP', '2024-07-30 14:00:00'::timestamp, '2024-07-30 15:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(116, 1, 'CONFIRMED', 'NO_NEED', '2024-07-30 16:00:00'::timestamp, '2024-07-30 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, 5),
(117, 1, 'PENDING', 'ELSE_WHERE', '2024-07-31 10:00:00'::timestamp, '2024-07-31 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, null),
(118, 1, 'CONFIRMED', 'ELSE_WHERE', '2024-07-31 13:00:00'::timestamp, '2024-07-31 14:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 6),
(119, 1, 'PENDING', 'ELSE_WHERE', '2024-07-31 16:00:00'::timestamp, '2024-07-31 17:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, true, null),
(120, 1, 'CONFIRMED', 'IN_SHOP', '2024-07-01 10:00:00'::timestamp, '2024-07-01 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7) ,
(121, 1, 'CONFIRMED', 'IN_SHOP', '2024-07-02 10:00:00'::timestamp, '2024-07-02 11:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false, 7) ) AS temp
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'admin@example.com');

-- 조건 정보 삽입
-- Insert condition information
INSERT INTO conditions (reservation_detail_id, option, created_at, modified_at)
SELECT * FROM (VALUES
(1, 'REPAIR', '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp),
(1, 'AS', '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp),
(1, 'WOUND_CARE', '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp),
(2, 'REPAIR', '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp),
(2, 'CORRECTION', '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp),
(2, 'WOUND_CARE', '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp),
(3, 'AS', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(3, 'CORRECTION', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(4, 'REPAIR', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
( 5, 'AS', '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp),
( 5, 'WOUND_CARE', '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp),
( 6, 'CORRECTION', '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp),
( 6, 'AS', '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp),
( 7, 'REPAIR', '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp),
( 7, 'WOUND_CARE', '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp),
( 8, 'CORRECTION', '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp),
( 8, 'AS', '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp),
( 9, 'REPAIR', '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
( 9, 'WOUND_CARE', '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
( 10, 'CORRECTION', '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp),
( 10, 'AS', '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp),
( 11, 'REPAIR', '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp),
( 11, 'WOUND_CARE', '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp),
( 12, 'CORRECTION', '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp),
( 12, 'AS', '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp),
( 13, 'REPAIR', '2024-06-30 09:00:00'::timestamp, '2024-06-30 09:00:00'::timestamp),
( 13, 'WOUND_CARE', '2024-06-30 09:00:00'::timestamp, '2024-06-30 09:00:00'::timestamp),
( 14, 'CORRECTION', '2024-06-30 10:00:00'::timestamp, '2024-06-30 10:00:00'::timestamp),
( 14, 'AS', '2024-06-30 10:00:00'::timestamp, '2024-06-30 10:00:00'::timestamp),
( 15, 'REPAIR', '2024-06-30 11:00:00'::timestamp, '2024-06-30 11:00:00'::timestamp),
( 15, 'WOUND_CARE', '2024-06-30 11:00:00'::timestamp, '2024-06-30 11:00:00'::timestamp),
( 16, 'CORRECTION', '2024-06-30 12:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp),
( 16, 'AS', '2024-06-30 12:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp),
( 17, 'REPAIR', '2024-06-30 13:00:00'::timestamp, '2024-06-30 13:00:00'::timestamp),
( 17, 'WOUND_CARE', '2024-06-30 13:00:00'::timestamp, '2024-06-30 13:00:00'::timestamp),
( 18, 'CORRECTION', '2024-06-30 14:00:00'::timestamp, '2024-06-30 14:00:00'::timestamp),
( 19, 'AS', '2024-06-30 15:00:00'::timestamp, '2024-06-30 15:00:00'::timestamp),
( 20, 'REPAIR', '2024-06-30 16:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp),
( 20, 'WOUND_CARE', '2024-06-30 16:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp),
( 21, 'CORRECTION', '2024-06-30 17:00:00'::timestamp, '2024-06-30 17:00:00'::timestamp),
( 21, 'AS', '2024-06-30 17:00:00'::timestamp, '2024-06-30 17:00:00'::timestamp),
( 22, 'REPAIR', '2024-06-30 18:00:00'::timestamp, '2024-06-30 18:00:00'::timestamp),
( 23, 'WOUND_CARE', '2024-06-30 19:00:00'::timestamp, '2024-06-30 19:00:00'::timestamp),
( 24, 'CORRECTION', '2024-06-30 20:00:00'::timestamp, '2024-06-30 20:00:00'::timestamp),
( 24, 'AS', '2024-06-30 20:00:00'::timestamp, '2024-06-30 20:00:00'::timestamp),
( 25, 'REPAIR', '2024-06-30 21:00:00'::timestamp, '2024-06-30 21:00:00'::timestamp),
( 26, 'WOUND_CARE', '2024-06-30 22:00:00'::timestamp, '2024-06-30 22:00:00'::timestamp),
( 27, 'CORRECTION', '2024-06-30 23:00:00'::timestamp, '2024-06-30 23:00:00'::timestamp),
( 28, 'AS', '2024-07-01 09:00:00'::timestamp, '2024-07-01 09:00:00'::timestamp),
( 29, 'REPAIR', '2024-07-01 10:00:00'::timestamp, '2024-07-01 10:00:00'::timestamp),
( 29, 'WOUND_CARE', '2024-07-01 10:00:00'::timestamp, '2024-07-01 10:00:00'::timestamp),
( 30, 'CORRECTION', '2024-07-01 11:00:00'::timestamp, '2024-07-01 11:00:00'::timestamp),
( 31, 'AS', '2024-07-01 12:00:00'::timestamp, '2024-07-01 12:00:00'::timestamp),
( 32, 'REPAIR', '2024-07-01 13:00:00'::timestamp, '2024-07-01 13:00:00'::timestamp),
( 32, 'WOUND_CARE', '2024-07-01 13:00:00'::timestamp, '2024-07-01 13:00:00'::timestamp),
( 33, 'CORRECTION', '2024-07-01 14:00:00'::timestamp, '2024-07-01 14:00:00'::timestamp),
( 34, 'AS', '2024-07-01 15:00:00'::timestamp, '2024-07-01 15:00:00'::timestamp),
( 35, 'REPAIR', '2024-07-01 16:00:00'::timestamp, '2024-07-01 16:00:00'::timestamp),
( 35, 'WOUND_CARE', '2024-07-01 16:00:00'::timestamp, '2024-07-01 16:00:00'::timestamp),
( 36, 'CORRECTION', '2024-07-01 17:00:00'::timestamp, '2024-07-01 17:00:00'::timestamp),
( 37, 'AS', '2024-07-01 18:00:00'::timestamp, '2024-07-01 18:00:00'::timestamp),
( 38, 'REPAIR', '2024-07-01 19:00:00'::timestamp, '2024-07-01 19:00:00'::timestamp),
( 38, 'WOUND_CARE', '2024-07-01 19:00:00'::timestamp, '2024-07-01 19:00:00'::timestamp),
( 39, 'CORRECTION', '2024-07-01 20:00:00'::timestamp, '2024-07-01 20:00:00'::timestamp),
( 40, 'AS', '2024-07-01 21:00:00'::timestamp, '2024-07-01 21:00:00'::timestamp),
( 41, 'REPAIR', '2024-07-01 22:00:00'::timestamp, '2024-07-01 22:00:00'::timestamp),
( 42, 'WOUND_CARE', '2024-07-01 23:00:00'::timestamp, '2024-07-01 23:00:00'::timestamp),
( 42, 'AS', '2024-07-01 23:00:00'::timestamp, '2024-07-01 23:00:00'::timestamp),
( 43, 'REPAIR', '2024-07-02 09:00:00'::timestamp, '2024-07-02 09:00:00'::timestamp),
( 44, 'WOUND_CARE', '2024-07-02 10:00:00'::timestamp, '2024-07-02 10:00:00'::timestamp),
( 44, 'AS', '2024-07-02 10:00:00'::timestamp, '2024-07-02 10:00:00'::timestamp),
( 45, 'CORRECTION', '2024-07-02 11:00:00'::timestamp, '2024-07-02 11:00:00'::timestamp),
( 46, 'REPAIR', '2024-07-02 12:00:00'::timestamp, '2024-07-02 12:00:00'::timestamp),
( 46, 'AS', '2024-07-02 12:00:00'::timestamp, '2024-07-02 12:00:00'::timestamp),
( 47, 'WOUND_CARE', '2024-07-02 13:00:00'::timestamp, '2024-07-02 13:00:00'::timestamp),
( 48, 'CORRECTION', '2024-07-02 14:00:00'::timestamp, '2024-07-02 14:00:00'::timestamp),
( 49, 'AS', '2024-07-02 15:00:00'::timestamp, '2024-07-02 15:00:00'::timestamp),
( 50, 'REPAIR', '2024-07-02 16:00:00'::timestamp, '2024-07-02 16:00:00'::timestamp),
( 51, 'WOUND_CARE', '2024-07-02 17:00:00'::timestamp, '2024-07-02 17:00:00'::timestamp),
( 51, 'CORRECTION', '2024-07-02 17:00:00'::timestamp, '2024-07-02 17:00:00'::timestamp),
( 52, 'AS', '2024-07-02 18:00:00'::timestamp, '2024-07-02 18:00:00'::timestamp),
( 53, 'REPAIR', '2024-07-02 19:00:00'::timestamp, '2024-07-02 19:00:00'::timestamp),
( 54, 'WOUND_CARE', '2024-07-02 20:00:00'::timestamp, '2024-07-02 20:00:00'::timestamp),
( 54, 'CORRECTION', '2024-07-02 20:00:00'::timestamp, '2024-07-02 20:00:00'::timestamp),
( 55, 'AS', '2024-07-02 21:00:00'::timestamp, '2024-07-02 21:00:00'::timestamp),
( 56, 'REPAIR', '2024-07-02 22:00:00'::timestamp, '2024-07-02 22:00:00'::timestamp),
( 57, 'WOUND_CARE', '2024-07-02 23:00:00'::timestamp, '2024-07-02 23:00:00'::timestamp),
( 57, 'CORRECTION', '2024-07-02 23:00:00'::timestamp, '2024-07-02 23:00:00'::timestamp),
( 58, 'AS', '2024-07-03 09:00:00'::timestamp, '2024-07-03 09:00:00'::timestamp),
( 59, 'REPAIR', '2024-07-03 10:00:00'::timestamp, '2024-07-03 10:00:00'::timestamp),
( 59, 'WOUND_CARE', '2024-07-03 10:00:00'::timestamp, '2024-07-03 10:00:00'::timestamp),
( 60, 'CORRECTION', '2024-07-03 11:00:00'::timestamp, '2024-07-03 11:00:00'::timestamp),
( 60, 'AS', '2024-07-03 11:00:00'::timestamp, '2024-07-03 11:00:00'::timestamp),
( 61, 'REPAIR', '2024-07-03 12:00:00'::timestamp, '2024-07-03 12:00:00'::timestamp),
( 62, 'WOUND_CARE', '2024-07-03 13:00:00'::timestamp, '2024-07-03 13:00:00'::timestamp),
( 63, 'CORRECTION', '2024-07-03 14:00:00'::timestamp, '2024-07-03 14:00:00'::timestamp),
( 63, 'AS', '2024-07-03 14:00:00'::timestamp, '2024-07-03 14:00:00'::timestamp),
( 64, 'REPAIR', '2024-07-03 15:00:00'::timestamp, '2024-07-03 15:00:00'::timestamp),
( 65, 'WOUND_CARE', '2024-07-03 16:00:00'::timestamp, '2024-07-03 16:00:00'::timestamp),
(66, 'CORRECTION', '2024-07-03 17:00:00'::timestamp, '2024-07-03 17:00:00'::timestamp),
(66, 'AS', '2024-07-03 17:00:00'::timestamp, '2024-07-03 17:00:00'::timestamp),
(67, 'REPAIR', '2024-07-03 18:00:00'::timestamp, '2024-07-03 18:00:00'::timestamp),
(68, 'WOUND_CARE', '2024-07-03 19:00:00'::timestamp, '2024-07-03 19:00:00'::timestamp),
(69, 'CORRECTION', '2024-07-03 20:00:00'::timestamp, '2024-07-03 20:00:00'::timestamp),
(69, 'AS', '2024-07-03 20:00:00'::timestamp, '2024-07-03 20:00:00'::timestamp),
(70, 'REPAIR', '2024-07-03 21:00:00'::timestamp, '2024-07-03 21:00:00'::timestamp),
(71, 'WOUND_CARE', '2024-07-03 22:00:00'::timestamp, '2024-07-03 22:00:00'::timestamp),
(72, 'CORRECTION', '2024-07-03 23:00:00'::timestamp, '2024-07-03 23:00:00'::timestamp),
(72, 'AS', '2024-07-03 23:00:00'::timestamp, '2024-07-03 23:00:00'::timestamp),
(73, 'REPAIR', '2024-07-04 09:00:00'::timestamp, '2024-07-04 09:00:00'::timestamp),
(74, 'WOUND_CARE', '2024-07-04 10:00:00'::timestamp, '2024-07-04 10:00:00'::timestamp),
(74, 'AS', '2024-07-04 10:00:00'::timestamp, '2024-07-04 10:00:00'::timestamp),
(75, 'CORRECTION', '2024-07-04 11:00:00'::timestamp, '2024-07-04 11:00:00'::timestamp),
(76, 'REPAIR', '2024-07-04 12:00:00'::timestamp, '2024-07-04 12:00:00'::timestamp),
(77, 'WOUND_CARE', '2024-07-04 13:00:00'::timestamp, '2024-07-04 13:00:00'::timestamp),
(78, 'CORRECTION', '2024-07-04 14:00:00'::timestamp, '2024-07-04 14:00:00'::timestamp),
(79, 'AS', '2024-07-04 15:00:00'::timestamp, '2024-07-04 15:00:00'::timestamp),
(80, 'REPAIR', '2024-07-04 16:00:00'::timestamp, '2024-07-04 16:00:00'::timestamp),
(81, 'WOUND_CARE', '2024-07-04 17:00:00'::timestamp, '2024-07-04 17:00:00'::timestamp),
(81, 'CORRECTION', '2024-07-04 17:00:00'::timestamp, '2024-07-04 17:00:00'::timestamp),
(82, 'AS', '2024-07-04 18:00:00'::timestamp, '2024-07-04 18:00:00'::timestamp),
(83, 'REPAIR', '2024-07-04 19:00:00'::timestamp, '2024-07-04 19:00:00'::timestamp),
(84, 'WOUND_CARE', '2024-07-04 20:00:00'::timestamp, '2024-07-04 20:00:00'::timestamp),
(84, 'CORRECTION', '2024-07-04 20:00:00'::timestamp, '2024-07-04 20:00:00'::timestamp),
(85, 'AS', '2024-07-04 21:00:00'::timestamp, '2024-07-04 21:00:00'::timestamp),
(86, 'REPAIR', '2024-07-04 22:00:00'::timestamp, '2024-07-04 22:00:00'::timestamp),
(87, 'WOUND_CARE', '2024-07-04 23:00:00'::timestamp, '2024-07-04 23:00:00'::timestamp),
(87, 'CORRECTION', '2024-07-04 23:00:00'::timestamp, '2024-07-04 23:00:00'::timestamp),
(88, 'AS', '2024-07-05 09:00:00'::timestamp, '2024-07-05 09:00:00'::timestamp),
(89, 'REPAIR', '2024-07-05 10:00:00'::timestamp, '2024-07-05 10:00:00'::timestamp),
(89, 'WOUND_CARE', '2024-07-05 10:00:00'::timestamp, '2024-07-05 10:00:00'::timestamp),
(90, 'CORRECTION', '2024-07-05 11:00:00'::timestamp, '2024-07-05 11:00:00'::timestamp),
(91, 'AS', '2024-07-05 12:00:00'::timestamp, '2024-07-05 12:00:00'::timestamp),
(92, 'REPAIR', '2024-07-05 13:00:00'::timestamp, '2024-07-05 13:00:00'::timestamp),
(93, 'WOUND_CARE', '2024-07-05 14:00:00'::timestamp, '2024-07-05 14:00:00'::timestamp),
(93, 'CORRECTION', '2024-07-05 14:00:00'::timestamp, '2024-07-05 14:00:00'::timestamp),
(94, 'AS', '2024-07-05 15:00:00'::timestamp, '2024-07-05 15:00:00'::timestamp),
(95, 'REPAIR', '2024-07-05 16:00:00'::timestamp, '2024-07-05 16:00:00'::timestamp),
(96, 'WOUND_CARE', '2024-07-05 17:00:00'::timestamp, '2024-07-05 17:00:00'::timestamp),
(96, 'CORRECTION', '2024-07-05 17:00:00'::timestamp, '2024-07-05 17:00:00'::timestamp),
(97, 'AS', '2024-07-05 18:00:00'::timestamp, '2024-07-05 18:00:00'::timestamp),
(98, 'REPAIR', '2024-07-05 19:00:00'::timestamp, '2024-07-05 19:00:00'::timestamp),
(99, 'WOUND_CARE', '2024-07-05 20:00:00'::timestamp, '2024-07-05 20:00:00'::timestamp),
(99, 'CORRECTION', '2024-07-05 20:00:00'::timestamp, '2024-07-05 20:00:00'::timestamp),
(100, 'AS', '2024-07-05 21:00:00'::timestamp, '2024-07-05 21:00:00'::timestamp),
(101, 'REPAIR', '2024-07-05 22:00:00'::timestamp, '2024-07-05 22:00:00'::timestamp),
(102, 'WOUND_CARE', '2024-07-05 23:00:00'::timestamp, '2024-07-05 23:00:00'::timestamp),
(102, 'CORRECTION', '2024-07-05 23:00:00'::timestamp, '2024-07-05 23:00:00'::timestamp),
(103, 'AS', '2024-07-06 09:00:00'::timestamp, '2024-07-06 09:00:00'::timestamp),
(104, 'REPAIR', '2024-07-06 10:00:00'::timestamp, '2024-07-06 10:00:00'::timestamp),
(104, 'WOUND_CARE', '2024-07-06 10:00:00'::timestamp, '2024-07-06 10:00:00'::timestamp),
(105, 'CORRECTION', '2024-07-06 11:00:00'::timestamp, '2024-07-06 11:00:00'::timestamp),
(106, 'AS', '2024-07-06 12:00:00'::timestamp, '2024-07-06 12:00:00'::timestamp),
(107, 'REPAIR', '2024-07-06 13:00:00'::timestamp, '2024-07-06 13:00:00'::timestamp),
(107, 'WOUND_CARE', '2024-07-06 13:00:00'::timestamp, '2024-07-06 13:00:00'::timestamp),
(108, 'CORRECTION', '2024-07-06 14:00:00'::timestamp, '2024-07-06 14:00:00'::timestamp),
(109, 'AS', '2024-07-06 15:00:00'::timestamp, '2024-07-06 15:00:00'::timestamp),
(110, 'REPAIR', '2024-07-06 16:00:00'::timestamp, '2024-07-06 16:00:00'::timestamp),
(110, 'WOUND_CARE', '2024-07-06 16:00:00'::timestamp, '2024-07-06 16:00:00'::timestamp),
(111, 'CORRECTION', '2024-07-06 17:00:00'::timestamp, '2024-07-06 17:00:00'::timestamp),
(112, 'AS', '2024-07-06 18:00:00'::timestamp, '2024-07-06 18:00:00'::timestamp),
(113, 'REPAIR', '2024-07-06 19:00:00'::timestamp, '2024-07-06 19:00:00'::timestamp),
(113, 'WOUND_CARE', '2024-07-06 19:00:00'::timestamp, '2024-07-06 19:00:00'::timestamp),
(114, 'CORRECTION', '2024-07-06 20:00:00'::timestamp, '2024-07-06 20:00:00'::timestamp),
(115, 'AS', '2024-07-06 21:00:00'::timestamp, '2024-07-06 21:00:00'::timestamp),
(116, 'REPAIR', '2024-07-06 22:00:00'::timestamp, '2024-07-06 22:00:00'::timestamp),
(117, 'WOUND_CARE', '2024-07-06 23:00:00'::timestamp, '2024-07-06 23:00:00'::timestamp),
(117, 'CORRECTION', '2024-07-06 23:00:00'::timestamp, '2024-07-06 23:00:00'::timestamp),
(118, 'AS', '2024-07-07 09:00:00'::timestamp, '2024-07-07 09:00:00'::timestamp),
(119, 'REPAIR', '2024-07-07 10:00:00'::timestamp, '2024-07-07 10:00:00'::timestamp),
(120, 'WOUND_CARE', '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp),
(120, 'CORRECTION', '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp),
(121, 'CORRECTION', '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp)) AS temp
WHERE NOT EXISTS (
    SELECT 1
    FROM conditions
    WHERE created_at >= '2024-06-25 11:00:00'::timestamp
        AND created_at <= '2024-07-07 11:00:00'::timestamp
);







-- 처리 정보 삽입
INSERT INTO treatments (reservation_detail_id, option, image_id, image_url, created_at, modified_at)
SELECT * FROM (VALUES
(1, 'AOM', 1, '', '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp),
(2, 'CARE', 2, '', '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp),
(3, 'ONE', 3, '', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(4, 'AOM', 4, '', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(5, 'AOM', 5, '', '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp),
(6, 'AOM', 6, '', '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp),
(7, 'CARE', 7, '', '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp),
(8, 'ONE', 8, '', '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp),
(9, 'AOM', 9, '', '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
(10, 'ONE', 10, '', '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp),
(11, 'ONE', 11, '', '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp),
(12, 'ONE', 12, '', '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp),
(13, 'AOM', 13, '', '2024-06-30 09:00:00'::timestamp, '2024-06-30 09:00:00'::timestamp),
(14, 'CARE', 14, '', '2024-06-30 10:00:00'::timestamp, '2024-06-30 10:00:00'::timestamp),
(15, 'ONE', 15, '', '2024-06-30 11:00:00'::timestamp, '2024-06-30 11:00:00'::timestamp),
(16, 'AOM', 16, '', '2024-06-30 12:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp),
(17, 'AOM', 17, '', '2024-06-30 13:00:00'::timestamp, '2024-06-30 13:00:00'::timestamp),
(18, 'CARE', 18, '', '2024-06-30 14:00:00'::timestamp, '2024-06-30 14:00:00'::timestamp),
(19, 'AOM', 19, '', '2024-06-30 15:00:00'::timestamp, '2024-06-30 15:00:00'::timestamp),
(20, 'AOM', 20, '', '2024-06-30 16:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp),
(21, 'AOM', 21, '', '2024-06-30 17:00:00'::timestamp, '2024-06-30 17:00:00'::timestamp),
(22, 'CARE', 22, '', '2024-06-30 18:00:00'::timestamp, '2024-06-30 18:00:00'::timestamp),
(23, 'ONE', 23, '', '2024-06-30 19:00:00'::timestamp, '2024-06-30 19:00:00'::timestamp),
(24, 'ONE', 24, '', '2024-06-30 20:00:00'::timestamp, '2024-06-30 20:00:00'::timestamp),
(25, 'AOM', 25, '', '2024-06-30 21:00:00'::timestamp, '2024-06-30 21:00:00'::timestamp),
(26, 'CARE', 26, '', '2024-06-30 22:00:00'::timestamp, '2024-06-30 22:00:00'::timestamp),
(27, 'ONE', 27, '', '2024-06-30 23:00:00'::timestamp, '2024-06-30 23:00:00'::timestamp),
(28, 'AOM', 28, '', '2024-07-01 09:00:00'::timestamp, '2024-07-01 09:00:00'::timestamp),
(29, 'CARE', 29, '', '2024-07-01 10:00:00'::timestamp, '2024-07-01 10:00:00'::timestamp),
(30, 'ONE', 30, '', '2024-07-01 11:00:00'::timestamp, '2024-07-01 11:00:00'::timestamp),
(31, 'AOM', 31, '', '2024-07-01 12:00:00'::timestamp, '2024-07-01 12:00:00'::timestamp),
(32, 'CARE', 32, '', '2024-07-01 13:00:00'::timestamp, '2024-07-01 13:00:00'::timestamp),
(33, 'ONE', 33, '', '2024-07-01 14:00:00'::timestamp, '2024-07-01 14:00:00'::timestamp),
(34, 'AOM', 34, '', '2024-07-01 15:00:00'::timestamp, '2024-07-01 15:00:00'::timestamp),
(35, 'CARE', 35, '', '2024-07-01 16:00:00'::timestamp, '2024-07-01 16:00:00'::timestamp),
(36, 'ONE', 36, '', '2024-07-01 17:00:00'::timestamp, '2024-07-01 17:00:00'::timestamp),
(37, 'AOM', 37, '', '2024-07-01 18:00:00'::timestamp, '2024-07-01 18:00:00'::timestamp),
(38, 'CARE', 38, '', '2024-07-01 19:00:00'::timestamp, '2024-07-01 19:00:00'::timestamp),
(39, 'ONE', 39, '', '2024-07-01 20:00:00'::timestamp, '2024-07-01 20:00:00'::timestamp),
(40, 'AOM', 40, '', '2024-07-01 21:00:00'::timestamp, '2024-07-01 21:00:00'::timestamp),
(41, 'CARE', 41, '', '2024-07-01 22:00:00'::timestamp, '2024-07-01 22:00:00'::timestamp),
(42, 'ONE', 42, '', '2024-07-01 23:00:00'::timestamp, '2024-07-01 23:00:00'::timestamp),
(43, 'AOM', 43, '', '2024-07-02 09:00:00'::timestamp, '2024-07-02 09:00:00'::timestamp),
(44, 'CARE', 44, '', '2024-07-02 10:00:00'::timestamp, '2024-07-02 10:00:00'::timestamp),
(45, 'ONE', 45, '', '2024-07-02 11:00:00'::timestamp, '2024-07-02 11:00:00'::timestamp),
(46, 'AOM', 46, '', '2024-07-02 12:00:00'::timestamp, '2024-07-02 12:00:00'::timestamp),
(47, 'CARE', 47, '', '2024-07-02 13:00:00'::timestamp, '2024-07-02 13:00:00'::timestamp),
(48, 'ONE', 48, '', '2024-07-02 14:00:00'::timestamp, '2024-07-02 14:00:00'::timestamp),
(49, 'AOM', 49, '', '2024-07-02 15:00:00'::timestamp, '2024-07-02 15:00:00'::timestamp),
(50, 'CARE', 50, '', '2024-07-02 16:00:00'::timestamp, '2024-07-02 16:00:00'::timestamp),
(51, 'ONE', 51, '', '2024-07-02 17:00:00'::timestamp, '2024-07-02 17:00:00'::timestamp),
(52, 'AOM', 52, '', '2024-07-02 18:00:00'::timestamp, '2024-07-02 18:00:00'::timestamp),
(53, 'CARE', 53, '', '2024-07-02 19:00:00'::timestamp, '2024-07-02 19:00:00'::timestamp),
(54, 'ONE', 54, '', '2024-07-02 20:00:00'::timestamp, '2024-07-02 20:00:00'::timestamp),
(55, 'AOM', 55, '', '2024-07-02 21:00:00'::timestamp, '2024-07-02 21:00:00'::timestamp),
(56, 'CARE', 56, '', '2024-07-02 22:00:00'::timestamp, '2024-07-02 22:00:00'::timestamp),
(57, 'ONE', 57, '', '2024-07-02 23:00:00'::timestamp, '2024-07-02 23:00:00'::timestamp),
(58, 'AOM', 58, '', '2024-07-03 09:00:00'::timestamp, '2024-07-03 09:00:00'::timestamp),
(59, 'CARE', 59, '', '2024-07-03 10:00:00'::timestamp, '2024-07-03 10:00:00'::timestamp),
(60, 'ONE', 60, '', '2024-07-03 11:00:00'::timestamp, '2024-07-03 11:00:00'::timestamp),
(61, 'AOM', 61, '', '2024-07-03 12:00:00'::timestamp, '2024-07-03 12:00:00'::timestamp),
(62, 'CARE', 62, '', '2024-07-03 13:00:00'::timestamp, '2024-07-03 13:00:00'::timestamp),
(63, 'ONE', 63, '', '2024-07-03 14:00:00'::timestamp, '2024-07-03 14:00:00'::timestamp),
(64, 'AOM', 64, '', '2024-07-03 15:00:00'::timestamp, '2024-07-03 15:00:00'::timestamp),
(65, 'CARE', 65, '', '2024-07-03 16:00:00'::timestamp, '2024-07-03 16:00:00'::timestamp),
(66, 'ONE', 66, '', '2024-07-03 17:00:00'::timestamp, '2024-07-03 17:00:00'::timestamp),
(67, 'AOM', 67, '', '2024-07-03 18:00:00'::timestamp, '2024-07-03 18:00:00'::timestamp),
(68, 'CARE', 68, '', '2024-07-03 19:00:00'::timestamp, '2024-07-03 19:00:00'::timestamp),
(69, 'ONE', 69, '', '2024-07-03 20:00:00'::timestamp, '2024-07-03 20:00:00'::timestamp),
(70, 'AOM', 70, '', '2024-07-03 21:00:00'::timestamp, '2024-07-03 21:00:00'::timestamp),
(71, 'CARE', 71, '', '2024-07-03 22:00:00'::timestamp, '2024-07-03 22:00:00'::timestamp),
(72, 'ONE', 72, '', '2024-07-03 23:00:00'::timestamp, '2024-07-03 23:00:00'::timestamp),
(73, 'AOM', 73, '', '2024-07-04 09:00:00'::timestamp, '2024-07-04 09:00:00'::timestamp),
(74, 'CARE', 74, '', '2024-07-04 10:00:00'::timestamp, '2024-07-04 10:00:00'::timestamp),
(75, 'ONE', 75, '', '2024-07-04 11:00:00'::timestamp, '2024-07-04 11:00:00'::timestamp),
(76, 'AOM', 76, '', '2024-07-04 12:00:00'::timestamp, '2024-07-04 12:00:00'::timestamp),
(77, 'CARE', 77, '', '2024-07-04 13:00:00'::timestamp, '2024-07-04 13:00:00'::timestamp),
(78, 'ONE', 78, '', '2024-07-04 14:00:00'::timestamp, '2024-07-04 14:00:00'::timestamp),
(79, 'AOM', 79, '', '2024-07-04 15:00:00'::timestamp, '2024-07-04 15:00:00'::timestamp),
(80, 'CARE', 80, '', '2024-07-04 16:00:00'::timestamp, '2024-07-04 16:00:00'::timestamp),
(81, 'ONE', 81, '', '2024-07-04 17:00:00'::timestamp, '2024-07-04 17:00:00'::timestamp),
(82, 'AOM', 82, '', '2024-07-04 18:00:00'::timestamp, '2024-07-04 18:00:00'::timestamp),
(83, 'CARE', 83, '', '2024-07-04 19:00:00'::timestamp, '2024-07-04 19:00:00'::timestamp),
(84, 'ONE', 84, '', '2024-07-04 20:00:00'::timestamp, '2024-07-04 20:00:00'::timestamp),
(85, 'AOM', 85, '', '2024-07-04 21:00:00'::timestamp, '2024-07-04 21:00:00'::timestamp),
(86, 'CARE', 86, '', '2024-07-04 22:00:00'::timestamp, '2024-07-04 22:00:00'::timestamp),
(87, 'ONE', 87, '', '2024-07-04 23:00:00'::timestamp, '2024-07-04 23:00:00'::timestamp),
(88, 'AOM', 88, '', '2024-07-05 09:00:00'::timestamp, '2024-07-05 09:00:00'::timestamp),
(89, 'CARE', 89, '', '2024-07-05 10:00:00'::timestamp, '2024-07-05 10:00:00'::timestamp),
(90, 'ONE', 90, '', '2024-07-05 11:00:00'::timestamp, '2024-07-05 11:00:00'::timestamp),
(91, 'AOM', 91, '', '2024-07-05 12:00:00'::timestamp, '2024-07-05 12:00:00'::timestamp),
(92, 'CARE', 92, '', '2024-07-05 13:00:00'::timestamp, '2024-07-05 13:00:00'::timestamp),
(93, 'ONE', 93, '', '2024-07-05 14:00:00'::timestamp, '2024-07-05 14:00:00'::timestamp),
(94, 'AOM', 94, '', '2024-07-05 15:00:00'::timestamp, '2024-07-05 15:00:00'::timestamp),
(95, 'CARE', 95, '', '2024-07-05 16:00:00'::timestamp, '2024-07-05 16:00:00'::timestamp),
(96, 'ONE', 96, '', '2024-07-05 17:00:00'::timestamp, '2024-07-05 17:00:00'::timestamp),
(97, 'AOM', 97, '', '2024-07-05 18:00:00'::timestamp, '2024-07-05 18:00:00'::timestamp),
(98, 'CARE', 98, '', '2024-07-05 19:00:00'::timestamp, '2024-07-05 19:00:00'::timestamp),
(99, 'ONE', 99, '', '2024-07-05 20:00:00'::timestamp, '2024-07-05 20:00:00'::timestamp),
(100, 'AOM', 100, '', '2024-07-05 21:00:00'::timestamp, '2024-07-05 21:00:00'::timestamp),
(101, 'CARE', 101, '', '2024-07-05 22:00:00'::timestamp, '2024-07-05 22:00:00'::timestamp),
(102, 'ONE', 102, '', '2024-07-05 23:00:00'::timestamp, '2024-07-05 23:00:00'::timestamp),
(103, 'AOM', 103, '', '2024-07-06 09:00:00'::timestamp, '2024-07-06 09:00:00'::timestamp),
(104, 'CARE', 104, '', '2024-07-06 10:00:00'::timestamp, '2024-07-06 10:00:00'::timestamp),
(105, 'ONE', 105, '', '2024-07-06 11:00:00'::timestamp, '2024-07-06 11:00:00'::timestamp),
(106, 'AOM', 106, '', '2024-07-06 12:00:00'::timestamp, '2024-07-06 12:00:00'::timestamp),
(107, 'CARE', 107, '', '2024-07-06 13:00:00'::timestamp, '2024-07-06 13:00:00'::timestamp),
(108, 'ONE', 108, '', '2024-07-06 14:00:00'::timestamp, '2024-07-06 14:00:00'::timestamp),
(109, 'AOM', 109, '', '2024-07-06 15:00:00'::timestamp, '2024-07-06 15:00:00'::timestamp),
(110, 'CARE', 110, '', '2024-07-06 16:00:00'::timestamp, '2024-07-06 16:00:00'::timestamp),
(111, 'ONE', 111, '', '2024-07-06 17:00:00'::timestamp, '2024-07-06 17:00:00'::timestamp),
(112, 'AOM', 112, '', '2024-07-06 18:00:00'::timestamp, '2024-07-06 18:00:00'::timestamp),
(113, 'CARE', 113, '', '2024-07-06 19:00:00'::timestamp, '2024-07-06 19:00:00'::timestamp),
(114, 'ONE', 114, '', '2024-07-06 20:00:00'::timestamp, '2024-07-06 20:00:00'::timestamp),
(115, 'AOM', 115, '', '2024-07-06 21:00:00'::timestamp, '2024-07-06 21:00:00'::timestamp),
(116, 'CARE', 116, '', '2024-07-06 22:00:00'::timestamp, '2024-07-06 22:00:00'::timestamp),
(117, 'ONE', 117, '', '2024-07-06 23:00:00'::timestamp, '2024-07-06 23:00:00'::timestamp),
(118, 'AOM', 118, '', '2024-07-07 09:00:00'::timestamp, '2024-07-07 09:00:00'::timestamp),
(119, 'CARE', 119, '', '2024-07-07 10:00:00'::timestamp, '2024-07-07 10:00:00'::timestamp),
(120, 'ONE', 120, '', '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp),
(121, 'AOM', 120, '', '2024-07-07 11:00:00'::timestamp, '2024-07-07 11:00:00'::timestamp)) AS temp
WHERE NOT EXISTS (
    SELECT 1
    FROM treatments
    WHERE created_at >= '2024-06-25 11:00:00'::timestamp
        AND created_at <= '2024-07-07 11:00:00'::timestamp
);


-- 리뷰 데이터 삽입
INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 1,1, '친절한 서비스와 아름다운 네일 아트에 감동했습니다. 다음에도 꼭 방문할게요!', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 1);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 2,2 ,'디자인이 마음에 들었지만, 예약 시간이 조금 지연되어 아쉬웠어요.', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 2);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT  1, 3,3, '깨끗하고 편안한 분위기에서 네일을 받을 수 있어서 좋았습니다.', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 3);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT  1, 4,4, '매번 새로운 디자인을 제안해주셔서 너무 만족스럽습니다!', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 4);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 5,5, '네일 유지력이 좋아서 오랜 시간 예쁜 상태로 유지됩니다. 추천해요!', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 5);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 6,6, '가격 대비 서비스가 조금 아쉬웠어요. 그래도 디자인은 만족스럽습니다.', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 6);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 7,7, '친구 소개로 왔는데 정말 좋네요! 다음에도 또 오고 싶어요.', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 7);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 8,8, '직원분들이 정말 친절하고 세심하게 신경 써주셔서 만족스러웠습니다.', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 8);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 9,9, '디자인 선택에 있어서 다양성이 좀 더 있었으면 좋겠어요.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 9);

INSERT INTO review (shop_id, member_id,reservation_detail_id, contents, rating, created_at, modified_at)
SELECT 1, 10,10 ,'전체적으로 만족하지만, 예약 잡기가 조금 힘들었어요.', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM review WHERE review_id = 10);

-- 게시물 데이터 삽입
INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '4월 이달의 아트 이벤트', 'NEWS', '<4월 이달의 아트> 봄 느낌 물씬 풍기는 이달의 아트! 이벤트가로 할인 진행합니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '4월 이달의 아트 이벤트');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '5월 이달의 아트 소개', 'NEWS', '<5월 이달의 아트> 여름 느낌 물씬 풍기는 이달의 아트! 지금 예약하고 특별 할인을 받아보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '5월 이달의 아트 소개');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '여름 네일 할인 이벤트', 'NEWS', '<5월 이달의 아트> 여름 느낌 물씬 풍기는 네일 아트를 할인된 가격에 만나보세요!', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '여름 네일 할인 이벤트');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '5월의 특별한 아트', 'NEWS', '<5월 이달의 아트> 이번 달 특별한 아트를 할인된 가격으로 만나보세요!', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '5월의 특별한 아트');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '여름 네일 아트 이벤트', 'NEWS', '<5월 이달의 아트> 여름 느낌 가득한 네일 아트를 특별 이벤트 가격으로 제공합니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '여름 네일 아트 이벤트');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '특별 할인 네일 아트', 'NEWS', '<5월 이달의 아트> 여름 분위기를 담은 네일 아트를 특별 할인가로 만나보세요!', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '특별 할인 네일 아트');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '긴급 공지', 'NOTICE', '안녕하세요 고객님, 시스템 점검으로 인해 7월 15일 오전 10시부터 12시까지 서비스 이용이 제한됩니다. 불편을 드려 죄송합니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '긴급 공지');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '공지사항', 'NOTICE', '안녕하세요 고객님, 새로운 아트 디자인이 입고되었습니다. 많은 관심 부탁드립니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '공지사항');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '영업시간 변경 안내', 'NOTICE', '안녕하세요 고객님, 8월부터 매장 영업시간이 오전 10시에서 오후 8시로 변경됩니다. 방문에 참고 부탁드립니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '영업시간 변경 안내');

INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT 1, '매장 이전 안내', 'NOTICE', '안녕하세요 고객님, 저희 매장이 9월 1일부터 새로운 위치로 이전합니다. 새로운 주소는 서울시 강남구 테헤란로 123입니다.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '매장 이전 안내');


-- 이달의 아트 데이터 삽입
INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '에메랄드 바다 네일', '투명한 에메랄드 빛 바다를 닮은 네일 아트로, 시원한 여름을 연출해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '에메랄드 바다 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '썸머 나이트 글로우', '여름 밤의 낭만을 담은 반짝이는 글로우 네일 아트로, 손끝을 화려하게 빛내보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '썸머 나이트 글로우');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '트로피컬 프루츠 네일', '여름의 청량함을 가득 담은 열대 과일 모티브의 네일 아트로 상큼함을 더해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '트로피컬 프루츠 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '로맨틱 플라워 네일', '우아하고 세련된 플라워 패턴 네일 아트로, 손끝에 로맨틱한 감성을 더해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '로맨틱 플라워 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '여름빛 라벤더 네일', '여름 햇살을 닮은 라벤더 색상의 네일 아트로, 은은한 멋을 연출해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '여름빛 라벤더 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '선셋 오렌지 네일', '노을 지는 여름 하늘을 표현한 선셋 오렌지 네일 아트로 따뜻한 분위기를 완성해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '선셋 오렌지 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '코랄 리프 네일', '산호초의 아름다움을 담은 코랄 리프 네일 아트로, 활기찬 여름을 맞이해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '코랄 리프 네일');

INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT 1, '실버 샌드 네일', '은빛 모래사장을 연상시키는 실버 샌드 네일 아트로, 고급스러운 여름 스타일을 연출해보세요.', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM monthly_art WHERE title = '실버 샌드 네일');

INSERT INTO shop_liked_member (member_id, shop_id, created_at, modified_at)
SELECT
    m.member_id,
    s.shop_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    members m
        JOIN (
        SELECT shop_id, ROW_NUMBER() OVER (ORDER BY RANDOM()) as rn
        FROM shops
    ) s ON s.rn <= CAST(RANDOM() * 5 AS INT) + 1
WHERE
    NOT EXISTS (
        SELECT 1
        FROM shop_liked_member slm
        WHERE slm.member_id = m.member_id AND slm.shop_id = s.shop_id
    );

INSERT INTO shop_liked_member (member_id, shop_id, created_at, modified_at)
SELECT
    1 as member_id,
    s.shop_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    (SELECT shop_id, ROW_NUMBER() OVER (ORDER BY RANDOM()) as rn
     FROM shops
     ORDER BY RANDOM()
         LIMIT 5) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM shop_liked_member slm
        WHERE slm.member_id = 1 AND slm.shop_id = s.shop_id
    )
    LIMIT FLOOR(1 + RANDOM() * 5);
