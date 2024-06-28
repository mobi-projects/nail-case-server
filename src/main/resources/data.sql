INSERT INTO members (name, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'User', 'user@example.com', 'USER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'user@example.com');

INSERT INTO members (name, email, role, profile_img_url, social_type, social_id, created_at, modified_at)
SELECT 'Admin', 'admin@example.com', 'OWNER', 'https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c', 'KAKAO', '3588226794', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'admin@example.com');

INSERT INTO shops (shop_name, phone, available_seat, created_at, modified_at, owner_id)
SELECT '영록', '01050114190', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, m.member_id
FROM members m WHERE m.email = 'admin@example.com' AND NOT EXISTS (SELECT 1 FROM shops s WHERE s.shop_name = '영록');


-- 예약(reservations) 데이터 삽입
INSERT INTO reservations (reservation_id, shop_id, member_id, created_at, modified_at)
VALUES
(1, 1, 1, '2024-06-27 11:00:00', '2024-06-27 11:00:00'),
(2, 1, 1, '2024-06-27 12:00:00', '2024-06-27 12:00:00'),
(3, 1, 1, '2024-06-27 15:00:00', '2024-06-27 15:00:00'),
(4, 1, 1, '2024-06-27 15:00:00', '2024-06-27 15:00:00'),
(5, 1, 1, '2024-06-27 16:00:00', '2024-06-27 16:00:00'),
(6, 1, 1, '2024-06-27 17:00:00', '2024-06-27 17:00:00'),
(7, 1, 1, '2024-06-27 18:00:00', '2024-06-27 18:00:00'),
(8, 1, 1, '2024-06-27 19:00:00', '2024-06-27 19:00:00'),
(9, 1, 1, '2024-06-28 20:00:00', '2024-06-28 20:00:00'),
(10, 1, 1, '2024-06-28 20:00:00', '2024-06-28 20:00:00'),
(11, 1, 1, '2024-06-28 22:00:00', '2024-06-28 22:00:00'),
(12, 1, 1, '2024-06-29 20:00:00', '2024-06-29 20:00:00');

-- 예약 상세 정보 삽입
INSERT INTO reservation_details (reservation_id, status, remove, start_time, end_time, created_at, modified_at, extend) VALUES
(1, 'CONFIRMED', 'IN_SHOP', '2024-06-26 11:00:00'::timestamp, '2024-06-26 13:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp, false),
(2, 'CONFIRMED', 'ELSE_WHERE', '2024-06-26 14:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp, false),
(3, 'PENDING', 'ELSE_WHERE', '2024-06-27 09:00:00'::timestamp, '2024-06-27 11:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, false),
(4, 'CONFIRMED', 'NO_NEED', '2024-06-27 13:00:00'::timestamp, '2024-06-27 14:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp, false),
(5, 'CANCELED', 'NO_NEED', '2024-06-27 15:00:00'::timestamp, '2024-06-27 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp, false),
(6, 'PENDING', 'IN_SHOP', '2024-06-28 10:00:00'::timestamp, '2024-06-28 11:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp, false),
(7, 'PENDING', 'IN_SHOP', '2024-06-28 14:00:00'::timestamp, '2024-06-28 15:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp, false),
(8, 'CONFIRMED', 'NO_NEED', '2024-06-29 09:00:00'::timestamp, '2024-06-29 11:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp, false),
(9, 'CONFIRMED', 'IN_SHOP', '2024-06-29 13:00:00'::timestamp, '2024-06-29 14:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp, false),
(10, 'REJECTED', 'NO_NEED', '2024-06-29 16:00:00'::timestamp, '2024-06-29 18:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp, false),
(11, 'CONFIRMED', 'IN_SHOP', '2024-06-30 10:00:00'::timestamp, '2024-06-30 12:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp, false),
(12, 'CONFIRMED', 'IN_SHOP', '2024-06-30 14:00:00'::timestamp, '2024-06-30 16:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp, false);

-- 조건 정보 삽입
INSERT INTO conditions (reservation_detail_id, option, created_at, modified_at) VALUES
(1, 'REPAIR', '2024-06-25 11:00:00'::timestamp, '2024-06-25 11:00:00'::timestamp),
(2, 'REPAIR', '2024-06-25 12:00:00'::timestamp, '2024-06-25 12:00:00'::timestamp),
(3, 'WOUND_CARE', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(4, 'REPAIR', '2024-06-26 15:00:00'::timestamp, '2024-06-26 15:00:00'::timestamp),
(5, 'REPAIR', '2024-06-26 16:00:00'::timestamp, '2024-06-26 16:00:00'::timestamp),
(6, 'CORRECTION', '2024-06-27 17:00:00'::timestamp, '2024-06-27 17:00:00'::timestamp),
(7, 'CORRECTION', '2024-06-27 18:00:00'::timestamp, '2024-06-27 18:00:00'::timestamp),
(8, 'REPAIR', '2024-06-28 19:00:00'::timestamp, '2024-06-28 19:00:00'::timestamp),
(9, 'WOUND_CARE', '2024-06-28 20:00:00'::timestamp, '2024-06-28 20:00:00'::timestamp),
(10, 'REPAIR', '2024-06-28 21:00:00'::timestamp, '2024-06-28 21:00:00'::timestamp),
(11, 'REPAIR', '2024-06-29 22:00:00'::timestamp, '2024-06-29 22:00:00'::timestamp),
(12, 'REPAIR', '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp);

-- 처리 정보 삽입
INSERT INTO treatments (reservation_detail_id, option, image_id, image_url, created_at, modified_at) VALUES
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
 (12, 'ONE', 12, '', '2024-06-29 23:00:00'::timestamp, '2024-06-29 23:00:00'::timestamp);