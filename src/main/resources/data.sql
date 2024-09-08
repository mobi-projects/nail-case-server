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

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '박지현', 'park@example.com', 'MANAGER',
       'https://example.com/images/park_profile.jpg',
       'KAKAO', '4567890123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'park@example.com');

-- Step 2: shops 테이블에 데이터를 삽입 (관리자의 nail_artist_id 사용)
INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일 스튜디오 더 로즈', '025551234', 3, '강남구 신사동 663-16 KR 서울특별시 강남구 가로수길 43 2층',
       '네일 스튜디오 더 로즈에서 여러분의 아름다움을 꽃피워드리겠습니다. 고급스러운 분위기와 전문적인 서비스로 여러분을 기다리고 있습니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'park@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일 스튜디오 더 로즈');

-- 네일 스튜디오 더 로즈의 WorkHour 데이터 삽입
INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:00:00'::time AS open_time, '20:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 2, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 3, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 4, true, '10:00:00', '20:00:00'
         UNION ALL SELECT 5, true, '10:00:00', '21:00:00'
         UNION ALL SELECT 6, true, '11:00:00', '19:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일 스튜디오 더 로즈'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 뷰티풀 핑거 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '이미란', 'lee@example.com', 'MANAGER',
       'https://example.com/images/lee_profile.jpg',
       'KAKAO', '7890123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'lee@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '뷰티풀 핑거', '025557890', 4, '서울특별시 강남구 역삼동 823-24 2층',
       '뷰티풀 핑거에서 당신의 손끝을 아름답게 만들어드립니다. 최신 트렌드와 개성 있는 디자인으로 여러분을 맞이합니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'lee@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '뷰티풀 핑거');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '11:00:00'::time AS open_time, '21:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 2, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 3, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 4, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 5, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 6, true, '12:00:00', '20:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '뷰티풀 핑거'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);
-- 네일아트 퀸 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '최예진', 'choi@example.com', 'MANAGER',
       'https://example.com/images/choi_profile.jpg',
       'KAKAO', '2345678901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'choi@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일아트 퀸', '025559876', 2, '서울특별시 서초구 서초동 1303-22 3층',
       '네일아트 퀸에서 여러분의 손톱을 왕비의 손처럼 아름답게 꾸며드립니다. 고급스러운 서비스와 섬세한 디자인으로 여러분을 기다립니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'choi@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일아트 퀸');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:30:00'::time AS open_time, '20:30:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:30:00', '20:30:00'
         UNION ALL SELECT 2, true, '10:30:00', '20:30:00'
         UNION ALL SELECT 3, true, '10:30:00', '20:30:00'
         UNION ALL SELECT 4, true, '10:30:00', '20:30:00'
         UNION ALL SELECT 5, true, '10:30:00', '21:30:00'
         UNION ALL SELECT 6, false, '00:00:00', '00:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일아트 퀸'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);
-- 네일 팩토리 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '송민주', 'song@example.com', 'MANAGER',
       'https://example.com/images/song_profile.jpg',
       'KAKAO', '8901234567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'song@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일 팩토리', '025553456', 5, '서울특별시 강남구 청담동 89-4 1층',
       '네일 팩토리에서 당신만의 특별한 네일 디자인을 만들어보세요. 다양한 스타일과 최신 트렌드를 한 곳에서 경험할 수 있습니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'song@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일 팩토리');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '09:30:00'::time AS open_time, '21:30:00'::time AS close_time
         UNION ALL SELECT 1, true, '09:30:00', '21:30:00'
         UNION ALL SELECT 2, true, '09:30:00', '21:30:00'
         UNION ALL SELECT 3, true, '09:30:00', '21:30:00'
         UNION ALL SELECT 4, true, '09:30:00', '21:30:00'
         UNION ALL SELECT 5, true, '09:30:00', '22:00:00'
         UNION ALL SELECT 6, true, '10:00:00', '20:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일 팩토리'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 6. 글램 네일 스튜디오 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '정아영', 'jung@example.com', 'MANAGER',
       'https://example.com/images/jung_profile.jpg',
       'KAKAO', '1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'jung@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '글램 네일 스튜디오', '025554321', 3, '서울특별시 강남구 신사동 663-16 2층',
       '글램 네일 스튜디오에서 화려하고 세련된 네일아트를 경험해보세요. 최신 트렌드와 고급 제품으로 여러분의 스타일을 완성시켜 드립니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'jung@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '글램 네일 스튜디오');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:00:00'::time AS open_time, '22:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 2, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 3, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 4, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 5, true, '10:00:00', '23:00:00'
         UNION ALL SELECT 6, true, '11:00:00', '21:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '글램 네일 스튜디오'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 7. 프렌치 네일 살롱 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '김서연', 'kim2@example.com', 'MANAGER',
       'https://example.com/images/kim2_profile.jpg',
       'KAKAO', '9876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'kim2@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '프렌치 네일 살롱', '025558765', 4, '서울특별시 서초구 방배동 450-3 1층',
       '프렌치 네일 살롱에서 클래식하고 세련된 프렌치 네일을 만나보세요. 정교한 기술과 고급 재료로 오래 유지되는 네일아트를 제공합니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'kim2@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '프렌치 네일 살롱');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '11:00:00'::time AS open_time, '20:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '11:00:00', '20:00:00'
         UNION ALL SELECT 2, true, '11:00:00', '20:00:00'
         UNION ALL SELECT 3, true, '11:00:00', '20:00:00'
         UNION ALL SELECT 4, true, '11:00:00', '20:00:00'
         UNION ALL SELECT 5, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 6, false, '00:00:00', '00:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '프렌치 네일 살롱'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 8. 네일 아트 갤러리 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '이지은', 'lee2@example.com', 'MANAGER',
       'https://example.com/images/lee2_profile.jpg',
       'KAKAO', '5432109876', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'lee2@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일 아트 갤러리', '025559999', 5, '서울특별시 강남구 청담동 121-5 3층',
       '네일 아트 갤러리에서 당신의 손톱을 캔버스 삼아 예술 작품을 만들어보세요. 독창적인 디자인과 섬세한 기술로 유니크한 네일아트를 선사합니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'lee2@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일 아트 갤러리');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '12:00:00'::time AS open_time, '21:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '12:00:00', '21:00:00'
         UNION ALL SELECT 2, true, '12:00:00', '21:00:00'
         UNION ALL SELECT 3, true, '12:00:00', '21:00:00'
         UNION ALL SELECT 4, true, '12:00:00', '21:00:00'
         UNION ALL SELECT 5, true, '12:00:00', '22:00:00'
         UNION ALL SELECT 6, true, '13:00:00', '20:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일 아트 갤러리'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 9. 젤네일 스페셜리스트 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '박소연', 'park2@example.com', 'MANAGER',
       'https://example.com/images/park2_profile.jpg',
       'KAKAO', '6789012345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'park2@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '젤네일 스페셜리스트', '025551111', 3, '서울특별시 강남구 삼성동 159-1 코엑스몰 3층',
       '젤네일 스페셜리스트에서 오래 지속되는 완벽한 젤네일을 경험해보세요. 손톱 건강을 고려한 프리미엄 젤 제품으로 안전하고 아름다운 네일아트를 제공합니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'park2@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '젤네일 스페셜리스트');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:30:00'::time AS open_time, '21:30:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:30:00', '21:30:00'
         UNION ALL SELECT 2, true, '10:30:00', '21:30:00'
         UNION ALL SELECT 3, true, '10:30:00', '21:30:00'
         UNION ALL SELECT 4, true, '10:30:00', '21:30:00'
         UNION ALL SELECT 5, true, '10:30:00', '22:00:00'
         UNION ALL SELECT 6, true, '11:00:00', '21:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '젤네일 스페셜리스트'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);

-- 10. 네일 & 스파 힐링센터 데이터 삽입
INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '최다혜', 'choi2@example.com', 'MANAGER',
       'https://example.com/images/choi2_profile.jpg',
       'KAKAO', '3456789012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'choi2@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일 & 스파 힐링센터', '025552222', 6, '서울특별시 서초구 반포동 107-6 4층',
       '네일 & 스파 힐링센터에서 네일케어와 함께 온전한 휴식을 취해보세요. 편안한 분위기에서 고급 네일아트와 스파 서비스를 동시에 즐길 수 있습니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'choi2@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일 & 스파 힐링센터');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '10:00:00'::time AS open_time, '22:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 2, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 3, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 4, true, '10:00:00', '22:00:00'
         UNION ALL SELECT 5, true, '10:00:00', '23:00:00'
         UNION ALL SELECT 6, true, '11:00:00', '21:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일 & 스파 힐링센터'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);



-- 태그 생성
-- 태그 생성 및 생성 시간 기록
INSERT INTO tags (tag_name, created_at, modified_at)
VALUES
    ('네일맛집', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('주차가능', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('오마카세아트', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('친환경 소재', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('빠른 서비스', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('예약 우선', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('아트 전문', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('손상 복구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('풀컬러 옵션', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('세련된 디자인', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (tag_name) DO NOTHING;

INSERT INTO nail_artists (nickname, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT '임지영', 'lim@example.com', 'MANAGER',
       'https://example.com/images/lim_profile.jpg',
       'KAKAO', '5678901234', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM nail_artists WHERE email = 'lim@example.com');

INSERT INTO shops (shop_name, phone, available_seat, address, overview, created_at, modified_at, owner_id)
SELECT '네일 디자인 연구소', '025557777', 4, '서울특별시 용산구 이태원동 34-16 3층',
       '네일 디자인 연구소는 끊임없는 연구와 혁신으로 새로운 네일 트렌드를 만들어갑니다. 독특하고 창의적인 디자인을 원하시는 분들께 추천드립니다.',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, n.nail_artist_id
FROM nail_artists n
WHERE n.email = 'lim@example.com'
  AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '네일 디자인 연구소');

INSERT INTO work_hours (shop_id, day_of_week, is_open, open_time, close_time, created_at, modified_at)
SELECT s.shop_id, day_of_week, is_open, open_time, close_time, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (
         SELECT 0 AS day_of_week, true AS is_open, '11:00:00'::time AS open_time, '21:00:00'::time AS close_time
         UNION ALL SELECT 1, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 2, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 3, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 4, true, '11:00:00', '21:00:00'
         UNION ALL SELECT 5, true, '11:00:00', '22:00:00'
         UNION ALL SELECT 6, true, '12:00:00', '20:00:00'
     ) AS temp, shops s
WHERE s.shop_name = '네일 디자인 연구소'
  AND NOT EXISTS (
    SELECT 1 FROM work_hours
    WHERE shop_id = s.shop_id AND day_of_week = temp.day_of_week
);
-- Shop 1에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 1, 1, 1  -- 태그 1
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 1
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 1, 2, 2  -- 태그 2
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 2
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 1, 3, 3  -- 태그 3
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 3
);

-- Shop 2에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 2, 4, 1  -- 태그 4
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 4
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 2, 5, 2  -- 태그 5
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 5
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 2, 6, 3  -- 태그 6
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 6
);

-- Shop 3에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 3, 7, 1  -- 태그 7
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 7
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 3, 8, 2  -- 태그 8
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 8
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 3, 9, 3  -- 태그 9
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 9
);

-- Shop 4에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 4, 10, 1  -- 태그 10
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 10
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 4, 1, 2  -- 태그 1
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 11
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 4, 2, 3  -- 태그 2
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 12
);

-- Shop 5에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 5, 3, 1  -- 태그 3
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 13
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 5, 4, 2  -- 태그 4
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 14
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 5, 5, 3  -- 태그 5
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 15
);

-- Shop 6에 태그 매핑
INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 6, 6, 1  -- 태그 6
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 16
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 6, 7, 2  -- 태그 7
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 17
);

INSERT INTO tag_mapping (shop_id, tag_id, sort_order)
SELECT 6, 8, 3  -- 태그 8
    WHERE NOT EXISTS (
    SELECT 1 FROM tag_mapping WHERE tag_mapping_id = 18
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
    reservation_details_data AS (
SELECT
    r.shop_id,
    r.created_at AS start_time,
    na.nail_artist_id,
    CASE WHEN random() < 0.7 THEN 'CONFIRMED' ELSE 'PENDING' END AS status,
    (ARRAY['IN_SHOP', 'ELSE_WHERE', 'NO_NEED'])[floor(random() * 3 + 1)] AS remove,
    r.created_at + (ARRAY[INTERVAL '30 minutes', INTERVAL '1 hour', INTERVAL '2 hours'])[floor(random() * 3 + 1)] AS end_time,
    random() < 0.3 AS extend,
    r.member_id,
    r.created_at AS reservation_created_at
FROM reservation_data r
    CROSS JOIN LATERAL (
    SELECT nail_artist_id
    FROM nail_artists
    WHERE shop_id = r.shop_id
    ORDER BY random()
    LIMIT 1
    ) na
    JOIN work_hours wh ON wh.shop_id = r.shop_id AND wh.day_of_week = EXTRACT(DOW FROM r.created_at)
WHERE r.created_at::time BETWEEN wh.open_time AND wh.close_time
  AND r.rn <= 200
    ),
    inserted_reservation_details AS (
INSERT INTO reservation_details (shop_id, status, remove, start_time, end_time, created_at, modified_at, extend, nail_artist_id)
SELECT
    shop_id,
    status,
    remove,
    start_time,
    CASE WHEN status = 'CONFIRMED' THEN end_time ELSE NULL END,
    start_time - INTERVAL '1 day',
    start_time - INTERVAL '1 day',
    extend,
    nail_artist_id
FROM reservation_details_data
WHERE NOT EXISTS (
    SELECT 1
    FROM reservation_details existing
    WHERE existing.nail_artist_id = reservation_details_data.nail_artist_id
  AND (
    (existing.start_time <= reservation_details_data.start_time AND existing.end_time > reservation_details_data.start_time)
   OR (existing.start_time < reservation_details_data.end_time AND existing.end_time >= reservation_details_data.end_time)
   OR (reservation_details_data.start_time <= existing.start_time AND reservation_details_data.end_time >= existing.end_time)
    )
    )
    RETURNING reservation_detail_id, shop_id, created_at
    ),
    inserted_reservations AS (
INSERT INTO reservations (shop_id, member_id, nail_artist_id, reservation_detail_id, created_at, modified_at)
SELECT
    rdd.shop_id,
    rdd.member_id,
    rdd.nail_artist_id,
    ird.reservation_detail_id,
    rdd.reservation_created_at,
    rdd.reservation_created_at
FROM inserted_reservation_details ird
    JOIN reservation_details_data rdd ON ird.shop_id = rdd.shop_id AND ird.created_at = rdd.start_time - INTERVAL '1 day'
    RETURNING reservation_id
    ),
    inserted_treatments AS (
INSERT INTO treatments (option, image_id, image_url, created_at, modified_at)
SELECT
    (ARRAY['AOM', 'CARE', 'ONE'])[floor(random() * 3 + 1)] AS option,
    floor(random() * 1000 + 1)::int AS image_id,
    '' AS image_url,
    ird.created_at,
    ird.created_at
FROM inserted_reservation_details ird
    RETURNING treatment_id, created_at
    ),
    updated_reservation_details AS (
UPDATE reservation_details rd
SET treatment_id = it.treatment_id
FROM inserted_treatments it
WHERE rd.reservation_detail_id IN (SELECT reservation_detail_id FROM inserted_reservation_details)
  AND rd.created_at = it.created_at
    RETURNING rd.reservation_detail_id
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

-- reservation_details가 없는 reservation을 삭제합니다.
DELETE FROM reservations
WHERE reservation_id NOT IN (SELECT DISTINCT reservation_id FROM reservation_details);

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
INSERT INTO shop_info (point, parking_lot_cnt, available_cnt, info, price, created_at, modified_at)
SELECT
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
    NOT EXISTS (SELECT 1 FROM shop_info si WHERE si.shop_info_id = s.shop_id);

-- shop_info_id를 랜덤으로 shop 테이블에 업데이트
UPDATE shops
SET shop_info_id = subquery.shop_info_id
    FROM (
    SELECT s.shop_id, si.shop_info_id
    FROM shops s
    JOIN shop_info si ON si.shop_info_id = s.shop_id
    ORDER BY random()
    LIMIT 6
) AS subquery
WHERE shops.shop_id = subquery.shop_id;



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

-- 모비네일 강남점 아티스트
UPDATE nail_artists
SET shop_id=1
WHERE shop_id IS NULL;
