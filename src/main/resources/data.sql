-- 먼저 members와 nail_artists 테이블에 데이터 삽입
INSERT INTO members (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Member', 'member@example.com', 'MEMBER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'member@example.com');

WITH RECURSIVE seq(n) AS (
    SELECT 2
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
)
INSERT INTO members (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT
    'Member' || CAST(n AS VARCHAR),
    'member' || CAST(n AS VARCHAR) || '@example.com',
    'MEMBER',
    'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
    'KAKAO',
    '3588226794' || CAST(n AS VARCHAR),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seq
WHERE NOT EXISTS (
    SELECT 1 FROM members WHERE email = 'member' || CAST(n AS VARCHAR) || '@example.com'
);

-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Manager', 'manager@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226795', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'manager@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '모비네일 강남점', '01012341234', 8, '서울 강남구 봉은사로6길 29 1층 102호',
       '매달 네일 오마카세를 제공하는 디자인 맛집 모비네일 ' || E'\n' || '현재 당일 예약 가능합니다',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'manager@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '모비네일 강남점');

-- 모비네일 강남점의 shop_id를 가져옵니다.
-- 직접 쿼리에서 데이터를 가져와서 사용합니다.
INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '모비쌤', 'mobi1@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226796', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi1@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '비모쌤', 'mobi2@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226797', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi2@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '피넛쌤', 'mobi3@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226798', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi3@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '케이쌤', 'mobi4@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226799', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi4@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '제로쌤', 'mobi5@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226800', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi5@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '조이쌤', 'mobi6@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226811', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi6@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '제인쌤', 'mobi7@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226812', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi7@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '알루미늄쌤', 'mobi8@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226813', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'mobi8@example.com');

-- 모비네일 강남점의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:00:00'::time AS open_time, '20:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 2, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 3, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 4, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 5, false, '00:00:00', '00:00:00'
         UNION ALL SELECT 6, false, '00:00:00', '00:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '모비네일 강남점'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);


-- 러블리네일 강남점 데이터 삽입
-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '김수빈', 'kim@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226814', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'kim@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '러블리네일 강남점', '025820206', 2, '서초동 1319-13 KR 서울특별시 서초구 서초2동 현대타워 507호',
       '안녕하세요. 러블리네일아트 입니다.' || E'\n' || '저희 강남역네일 러블리는 10년 이상된 샵으로 최상의 서비스와 꼼꼼한 관리로 건강하고 아름다운 손 , 발 , 눈썹 관리를 지향합니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'kim@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '러블리네일 강남점');

-- 러블리네일 강남점의 shop_id를 가져옵니다.
-- 직접 쿼리에서 데이터를 가져와서 사용합니다.
INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '강동원', 'dongwon@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226815', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '러블리네일 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'dongwon@example.com');


-- 러블리네일 강남점의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '09:00:00'::time AS open_time, '21:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '09:00:00', '21:00:00'
         UNION ALL SELECT 2, true, '09:00:00', '21:00:00'
         UNION ALL SELECT 3, true, '09:00:00', '21:00:00'
         UNION ALL SELECT 4, true, '09:00:00', '21:00:00'
         UNION ALL SELECT 5, true, '09:00:00', '19:00:00'
         UNION ALL SELECT 6, false, '00:00:00', '00:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '러블리네일 강남점'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 레푸스강남선릉점 인디고네일 데이터 삽입
-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '김스타', 'kimStar@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226816', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'kimStar@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '레푸스강남선릉점 인디고네일', '025653009', 5, '서울 강남구 테헤란로57길 24 1층',
       '문제성 손,발관리 실무 경력 25년 이상된 원장과 부원장 포함 5인조 팀의 프로페셔널한 서비스를 경험해보세요~' || E'\n' ||
       '인디고 네일은 남녀노소 불문 건강하고 아름다운 손톱.발톱을 지향합니다. 어떤 상태라도 관리.시술.변신.유지 다양한 솔루션을 드립니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'kimStar@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '레푸스강남선릉점 인디고네일');

-- 레푸스강남선릉점 인디고네일의 shop_id를 가져옵니다.
-- 직접 쿼리에서 데이터를 가져와서 사용합니다.
INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '이스타', 'leeStar@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226817', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '레푸스강남선릉점 인디고네일'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'leeStar@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '장스타', 'jangStar@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226818', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '레푸스강남선릉점 인디고네일'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'jangStar@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '홍스타', 'hongStar@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226819', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '레푸스강남선릉점 인디고네일'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'hongStar@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '코스타', 'coStar@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226820', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '레푸스강남선릉점 인디고네일'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'coStar@example.com');


-- Step 5: 월요일부터 일요일까지의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '11:00:00'::time AS open_time, '22:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 2, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 3, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 4, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 5, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 6, false, '00:00:00', '00:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '레푸스강남선릉점 인디고네일'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 아트랩네일 신사점 데이터 삽입
-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '김아티스트', 'kimArt@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226820', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'kimArt@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '아트랩네일 신사점', '0261050021', 3, '서울 강남구 강남대로162길 27-15 1층 아트랩네일 스튜디오',
       '신사동 가로수길에 위치한 ''핫플레이스'' 아트랩 네일스튜디오 입니다.' || E'\n' ||
       '공간에서 주는 HIP한 무드와 15년 경력의 감각적인 네일선생님의 시술로, 기쁨과 아름다움을 동시에 누리실 수 있습니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'kimArt@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '아트랩네일 신사점');

-- 아트랩네일 신사점의 shop_id를 가져옵니다.
-- 직접 쿼리에서 데이터를 가져와서 사용합니다.
INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '홍아티스트', 'hongArt@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226821', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '아트랩네일 신사점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'hongArt@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, '장아티스트', 'jangArt@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226822', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '아트랩네일 신사점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'jangArt@example.com');


-- Step 5: 월요일부터 일요일까지의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '09:00:00'::time AS open_time, '18:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '09:00:00', '18:00:00'
         UNION ALL SELECT 2, true, '09:00:00', '18:00:00'
         UNION ALL SELECT 3, true, '09:00:00', '18:00:00'
         UNION ALL SELECT 4, true, '09:00:00', '18:00:00'
         UNION ALL SELECT 5, true, '10:00:00', '17:00:00'
         UNION ALL SELECT 6, true, '10:00:00', '17:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '아트랩네일 신사점'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 네일맑음 데이터 삽입
-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '혼자해', 'alone@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226823', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'alone@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일맑음', '025525433', 1, '서울 강남구 역삼로14길 18',
       '오랜 경력의 1인샵으로 편하고 퀄리티 있는 시술 및 서비스 면에서 고객 만족하실겁니다.' || E'\n' ||
       '매니큐어 패디큐어 젤 아크릴 왁싱 속눈썹연장 속눈썹펌 가능하구여' || E'\n' ||
       '회원권(금액권, 횟수권)이용하시면 더 많은 혜택을 제공받으실수 있습니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'alone@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일맑음');


-- Step 4: 월요일부터 일요일까지의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '08:00:00'::time AS open_time, '20:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '08:00:00', '20:00:00'
         UNION ALL SELECT 2, true, '08:00:00', '20:00:00'
         UNION ALL SELECT 3, true, '08:00:00', '20:00:00'
         UNION ALL SELECT 4, true, '08:00:00', '20:00:00'
         UNION ALL SELECT 5, true, '09:00:00', '19:00:00'
         UNION ALL SELECT 6, true, '09:00:00', '19:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일맑음'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 네일몰 강남점 데이터 삽입
-- Step 1: nail_artists 테이블에 관리자 데이터를 먼저 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'nail1', 'nail1@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226824', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'nail1@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일몰 강남점', '025351949', 4, '서울특별시 서초구 강남대로65길 10 K-Tower 6층',
       '안녕하세요~ 네일몰 강남점입니다. 좀더 가까이 네일아트재료를 접하실수 있도록 강남점에 오픈하였습니다.' || E'\n' ||
       '누구나 쉽게 네일아트를 접할 수 있도록 다양하고 저렴한 재품들로 구성된 네일몰 강남점입니다',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'nail1@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일몰 강남점');

-- 네일몰 강남점의 shop_id를 가져옵니다.
-- 직접 쿼리에서 데이터를 가져와서 사용합니다.
INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, 'nail2', 'nail2@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226825', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '네일몰 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'nail2@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, 'nail3', 'nail3@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226826', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '네일몰 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'nail3@example.com');

INSERT INTO nail_artists (shop_id, nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT s.shop_id, 'nail4', 'nail4@example.com', 'MANAGER',
       'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c',
       'KAKAO', '3588226827', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM shops s
WHERE s.shop_name = '네일몰 강남점'
  AND NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'nail4@example.com');


-- 네일몰 강남점의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:00:00'::time AS open_time, '22:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 2, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 3, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 4, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 5, true, '11:00:00', '20:00:00'
         UNION ALL SELECT 6, true, '11:00:00', '20:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일몰 강남점'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

WITH RECURSIVE shop_range AS (
    SELECT generate_series(1, 6) AS shop_id
),
               reservation_data AS (
                   SELECT
                       s.shop_id,
                       m.member_id AS member_id,
                       CURRENT_TIMESTAMP - (random() * INTERVAL '30 days') AS created_at,
                       ROW_NUMBER() OVER (PARTITION BY s.shop_id ORDER BY random()) AS rn
                   FROM shop_range s
                            CROSS JOIN LATERAL (
                       SELECT member_id
                       FROM members
                       ORDER BY random()
                           LIMIT 100
    ) m,
    generate_series(1, 200)
    ),
    inserted_reservations AS (
INSERT INTO reservations (shop_id, member_id, created_at, modified_at)
SELECT
    shop_id,
    member_id,
    created_at,
    created_at
FROM reservation_data
WHERE rn <= 200
ORDER BY shop_id, created_at
    RETURNING reservation_id, shop_id, created_at
    ),
    reservation_details_data AS (
SELECT
    r.reservation_id,
    r.shop_id,
    r.created_at AS start_time,
    na.nail_artist_id,
    CASE WHEN random() < 0.7 THEN 'CONFIRMED' ELSE 'PENDING' END AS status,
    (ARRAY['IN_SHOP', 'ELSE_WHERE', 'NO_NEED'])[floor(random() * 3 + 1)] AS remove,
    r.created_at + (ARRAY[INTERVAL '30 minutes', INTERVAL '1 hour', INTERVAL '2 hours'])[floor(random() * 3 + 1)] AS end_time,
    random() < 0.3 AS extend,
    gs.num AS detail_num
FROM inserted_reservations r
    CROSS JOIN LATERAL (
    SELECT nail_artist_id
    FROM nail_artists
    WHERE shop_id = r.shop_id
    ORDER BY random()
    LIMIT 1
    ) na
    CROSS JOIN LATERAL (
    SELECT generate_series(1, 1 + floor(random() * 2)::int) AS num
    ) gs
    JOIN work_hours wh ON wh.shop_id = r.shop_id AND wh.day_of_week = EXTRACT(DOW FROM r.created_at)
WHERE r.created_at::time BETWEEN wh.open_time AND wh.close_time
    ),
    inserted_reservation_details AS (
INSERT INTO reservation_details (reservation_id, shop_id, status, remove, start_time, end_time, created_at, modified_at, extend, nail_artist_id)
SELECT
    rd.reservation_id,
    rd.shop_id,
    rd.status,
    rd.remove,
    rd.start_time,
    CASE WHEN rd.status = 'CONFIRMED' THEN rd.end_time ELSE NULL END,
    rd.start_time - INTERVAL '1 day',
    rd.start_time - INTERVAL '1 day',
    rd.extend,
    rd.nail_artist_id
FROM reservation_details_data rd
WHERE NOT EXISTS (
    SELECT 1
    FROM reservation_details existing
    WHERE existing.nail_artist_id = rd.nail_artist_id
  AND existing.reservation_id != rd.reservation_id
  AND (
    (existing.start_time <= rd.start_time AND existing.end_time > rd.start_time)
   OR (existing.start_time < rd.end_time AND existing.end_time >= rd.end_time)
   OR (rd.start_time <= existing.start_time AND rd.end_time >= existing.end_time)
    )
    )
    RETURNING reservation_detail_id, reservation_id, created_at
    ),
    inserted_treatments AS (
INSERT INTO treatments (reservation_detail_id, option, image_id, image_url, created_at, modified_at)
SELECT
    ird.reservation_detail_id,
    (ARRAY['AOM', 'CARE', 'ONE'])[floor(random() * 3 + 1)] AS option,
    floor(random() * 1000 + 1)::int AS image_id,
    '' AS image_url,
    ird.created_at,
    ird.created_at
FROM inserted_reservation_details ird
    RETURNING treatment_id, reservation_detail_id
    ),
    inserted_conditions AS (
INSERT INTO conditions (reservation_detail_id, option, created_at, modified_at)
SELECT
    ird.reservation_detail_id,
    (ARRAY['REPAIR', 'AS', 'WOUND_CARE', 'CORRECTION'])[floor(random() * 4 + 1)] AS option,
    ird.created_at,
    ird.created_at
FROM inserted_reservation_details ird
    RETURNING condition_id, reservation_detail_id
    )
SELECT
    (SELECT COUNT(*) FROM reservations) AS reservations_count,
    (SELECT COUNT(*) FROM reservation_details) AS reservation_details_count,
    (SELECT COUNT(*) FROM treatments) AS treatments_count,
    (SELECT COUNT(*) FROM conditions) AS conditions_count;
-- 먼저 reservation_details가 없는 reservation을 찾습니다.
WITH reservations_without_details AS (
    SELECT r.reservation_id
    FROM reservations r
             LEFT JOIN reservation_details rd ON r.reservation_id = rd.reservation_id
    WHERE rd.reservation_id IS NULL
)
-- 그 다음, 해당 reservation을 삭제합니다.
DELETE FROM reservations
WHERE reservation_id IN (SELECT reservation_id FROM reservations_without_details);

-- 삭제 후 각 테이블의 레코드 수를 확인합니다.
SELECT
    (SELECT COUNT(*) FROM reservations) AS reservations_count,
    (SELECT COUNT(*) FROM reservation_details) AS reservation_details_count,
    (SELECT COUNT(*) FROM treatments) AS treatments_count,
    (SELECT COUNT(*) FROM conditions) AS conditions_count;


-- 리뷰 데이터 삽입
INSERT INTO review (shop_id, member_id, reservation_detail_id, contents, rating, created_at, modified_at)
SELECT
    s.shop_id,
    floor(random() * 100 + 1)::int as member_id,
        rd.reservation_detail_id,
    (ARRAY[
         '친절한 서비스와 아름다운 네일 아트에 감동했습니다. 다음에도 꼭 방문할게요!',
     '디자인이 마음에 들었지만, 예약 시간이 조금 지연되어 아쉬웠어요.',
     '깨끗하고 편안한 분위기에서 네일을 받을 수 있어서 좋았습니다.',
     '매번 새로운 디자인을 제안해주셔서 너무 만족스럽습니다!',
     '네일 유지력이 좋아서 오랜 시간 예쁜 상태로 유지됩니다. 추천해요!',
     '가격 대비 서비스가 조금 아쉬웠어요. 그래도 디자인은 만족스럽습니다.',
     '친구 소개로 왔는데 정말 좋네요! 다음에도 또 오고 싶어요.',
     '직원분들이 정말 친절하고 세심하게 신경 써주셔서 만족스러웠습니다.',
     '디자인 선택에 있어서 다양성이 좀 더 있었으면 좋겠어요.',
     '전체적으로 만족하지만, 예약 잡기가 조금 힘들었어요.'
         ])[floor(random() * 10 + 1)] as contents,
    floor(random() * 5 + 1)::int as rating,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    shops s
    CROSS JOIN LATERAL (
    SELECT reservation_detail_id
    FROM reservation_details
    WHERE shop_id = s.shop_id
    ORDER BY random()
    LIMIT 10
    ) rd
WHERE NOT EXISTS (
    SELECT 1 FROM review WHERE shop_id = s.shop_id
    );

-- 게시물 데이터 삽입
INSERT INTO posts (shop_id, title, category, contents, likes, views, created_at, modified_at)
SELECT
    s.shop_id,
    title || ' - ' || s.shop_name,
    category,
    contents,
    0,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    shops s,
    (VALUES
         ('4월 이달의 아트 이벤트', 'NEWS', '<4월 이달의 아트> 봄 느낌 물씬 풍기는 이달의 아트! 이벤트가로 할인 진행합니다.'),
         ('5월 이달의 아트 소개', 'NEWS', '<5월 이달의 아트> 여름 느낌 물씬 풍기는 이달의 아트! 지금 예약하고 특별 할인을 받아보세요.'),
         ('여름 네일 할인 이벤트', 'NEWS', '<5월 이달의 아트> 여름 느낌 물씬 풍기는 네일 아트를 할인된 가격에 만나보세요!'),
         ('5월의 특별한 아트', 'NEWS', '<5월 이달의 아트> 이번 달 특별한 아트를 할인된 가격으로 만나보세요!'),
         ('여름 네일 아트 이벤트', 'NEWS', '<5월 이달의 아트> 여름 느낌 가득한 네일 아트를 특별 이벤트 가격으로 제공합니다.'),
         ('특별 할인 네일 아트', 'NEWS', '<5월 이달의 아트> 여름 분위기를 담은 네일 아트를 특별 할인가로 만나보세요!'),
         ('긴급 공지', 'NOTICE', '안녕하세요 고객님, 시스템 점검으로 인해 7월 15일 오전 10시부터 12시까지 서비스 이용이 제한됩니다. 불편을 드려 죄송합니다.'),
         ('공지사항', 'NOTICE', '안녕하세요 고객님, 새로운 아트 디자인이 입고되었습니다. 많은 관심 부탁드립니다.'),
         ('영업시간 변경 안내', 'NOTICE', '안녕하세요 고객님, 8월부터 매장 영업시간이 오전 10시에서 오후 8시로 변경됩니다. 방문에 참고 부탁드립니다.'),
         ('매장 이전 안내', 'NOTICE', '안녕하세요 고객님, 저희 매장이 9월 1일부터 새로운 위치로 이전합니다. 새로운 주소는 서울시 강남구 테헤란로 123입니다.')
    ) AS t(title, category, contents)
WHERE NOT EXISTS (
    SELECT 1 FROM posts WHERE shop_id = s.shop_id AND title = t.title || ' - ' || s.shop_name
);

-- 이달의 아트 데이터 삽입
INSERT INTO monthly_art (shop_id, title, contents, likes, views, created_at, modified_at)
SELECT
    s.shop_id,
    title || ' - ' || s.shop_name,
    contents,
    0,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    shops s,
    (VALUES
         ('에메랄드 바다 네일', '투명한 에메랄드 빛 바다를 닮은 네일 아트로, 시원한 여름을 연출해보세요.'),
         ('썸머 나이트 글로우', '여름 밤의 낭만을 담은 반짝이는 글로우 네일 아트로, 손끝을 화려하게 빛내보세요.'),
         ('트로피컬 프루츠 네일', '여름의 청량함을 가득 담은 열대 과일 모티브의 네일 아트로 상큼함을 더해보세요.'),
         ('로맨틱 플라워 네일', '우아하고 세련된 플라워 패턴 네일 아트로, 손끝에 로맨틱한 감성을 더해보세요.'),
         ('여름빛 라벤더 네일', '여름 햇살을 닮은 라벤더 색상의 네일 아트로, 은은한 멋을 연출해보세요.'),
         ('선셋 오렌지 네일', '노을 지는 여름 하늘을 표현한 선셋 오렌지 네일 아트로 따뜻한 분위기를 완성해보세요.'),
         ('코랄 리프 네일', '산호초의 아름다움을 담은 코랄 리프 네일 아트로, 활기찬 여름을 맞이해보세요.'),
         ('실버 샌드 네일', '은빛 모래사장을 연상시키는 실버 샌드 네일 아트로, 고급스러운 여름 스타일을 연출해보세요.')
    ) AS t(title, contents)
WHERE NOT EXISTS (
    SELECT 1 FROM monthly_art WHERE shop_id = s.shop_id AND title = t.title || ' - ' || s.shop_name
);

-- ShopInfo 데이터 삽입
INSERT INTO shop_info (shop_id, point, parking_lot_cnt, available_cnt, info, price, created_at, modified_at)
SELECT
    s.shop_id,
    CONCAT(CAST(random() * 180 - 90 AS DECIMAL(10,7)), ', ', CAST(random() * 360 - 180 AS DECIMAL(10,7))) AS point,
    floor(random() * 10)::int AS parking_lot_cnt,
        floor(random() * 20 + 1)::int AS available_cnt,
        CASE floor(random() * 5)::int
        WHEN 0 THEN '편안한 분위기의 네일 샵입니다.'
        WHEN 1 THEN '최신 트렌드를 반영한 디자인을 제공합니다.'
        WHEN 2 THEN '경력 풍부한 네일 아티스트들이 서비스를 제공합니다.'
        WHEN 3 THEN '청결과 위생을 최우선으로 생각합니다.'
        ELSE '고객 만족을 위해 항상 노력하고 있습니다.'
END AS info,
    CASE floor(random() * 5)::int
        WHEN 0 THEN '기본 네일 아트: 30,000원부터'
        WHEN 1 THEN '젤 네일: 50,000원부터'
        WHEN 2 THEN '손톱 교정: 40,000원부터'
        WHEN 3 THEN '풀 패키지: 80,000원부터'
        ELSE '상세 가격은 문의 바랍니다.'
END AS price,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    shops s
WHERE
    NOT EXISTS (SELECT 1 FROM shop_info si WHERE si.shop_id = s.shop_id);

-- PriceImage 데이터 삽입 (ShopInfo와 1:1 관계)
INSERT INTO price_image (shop_info_id, created_at, modified_at)
SELECT
    si.shop_info_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM
    shop_info si
        JOIN
    shops s ON si.shop_id = s.shop_id
WHERE
    NOT EXISTS (SELECT 1 FROM price_image pi WHERE pi.shop_info_id = si.shop_info_id);