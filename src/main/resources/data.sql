-- user_category 테이블의 사용자 INSERT 쿼리
INSERT INTO user_category (id, created_at, updated_at, classification, depth, memo, title, parent_id) VALUES (1, '2024-04-09 01:47:55', '2024-04-09 01:47:55', 'ROLE_MASTER', 0, '관리자 대 카테고리', '관리자', null);
INSERT INTO user_category (id, created_at, updated_at, classification, depth, memo, title, parent_id) VALUES (2, '2024-04-09 01:47:56', '2024-04-09 01:47:56', 'ROLE_TEENAGER', 0, '유저 대 카테고리', '청소년', null);
INSERT INTO user_category (id, created_at, updated_at, classification, depth, memo, title, parent_id) VALUES (3, '2024-04-09 01:47:56', '2024-04-09 01:47:56', 'ROLE_WITHDRAWAL', 0, '탈퇴', '탈퇴', null);

INSERT INTO user_category (id, created_at, updated_at, classification, depth, memo, title, parent_id) VALUES (4, '2024-04-09 01:47:55', '2024-04-09 01:47:55', 'ROLE_ADMIN', 1, '어드민 최고 관리자 입니다.', '최고권한/관리자', 1);


-- 비번 admin /admin 인 Admin 테이블의 사용자 INSERT 쿼리 (관리자 계정)
INSERT INTO user_admin (id, memo, nickname, password, admin_about, admin_id, refresh_token, updated_at, created_at, user_category_id)
VALUES (1, '관리자 계정 메모', '관리자', '$2a$10$zrRQ7LECjH9CevfadmRTCe3W8.2eK3hUd.Kah8dBme/qpY0FPaByO', '관리자 계정입니다.', 'admin'
       , 'eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IlJPTEVfQURNSU4iLCJleHAiOjE3MDI1NTQ1NjIsImlhdCI6MTcwMjU1NDMwMywic3ViIjoiYWRtaW4ifQ.FJ7ZB7oZx3XxqAx6tDJJg0YgGXc7JMgfqLFiM33IEC0lpXl8J6e0GunxA4lKgESu_SXo7AAqJyx8lUyPkd2fzQ', now(), now(), 4);

-- 설문조사 카테고리 --
INSERT INTO survey_category (id, created_at, updated_at, classification, memo, title) VALUES (1, '2024-04-11 18:53:30', '2024-04-11 18:53:30', null, '조사', '설문지');
INSERT INTO survey_category (id, created_at, updated_at, classification, memo, title) VALUES (2, '2024-04-11 18:53:36', '2024-04-11 18:53:36', null, '설문지', '조사');
INSERT INTO survey_category (id, created_at, updated_at, classification, memo, title) VALUES (3, '2024-04-11 18:53:44', '2024-04-26 00:48:17', null, 'memo', '설문조사카테고리');


# 비번 user 인 User 테이블의 사용자 INSERT 쿼리
INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (1, now(), now(), '유저 계정 메모', '유저', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (2, now(), now(), '유저 계정 메모', '유저 계정', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user1', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (3, now(), now(), '유저 계정 메모', '유저 계정2', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user2', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (4, now(), now(), '유저 계정 메모', '유저 계정3', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user3', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (5, now(), now(), '유저 계정 메모', '유저 계정4', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user4', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (6, now(), now(), '유저 계정 메모', '유저 계정5', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user5', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (7, now(), now(), '유저 계정 메모', '유저 계정6', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user6', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (8, now(), now(), '유저 계정 메모', '유저 계정7', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user7', 2, 1 ,'CILP', 6);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (9, now(), now(), '유저 계정 메모', '유저 계정8', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user8', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (10, now(), now(), '유저 계정 메모', '유저 계정9', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user9', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (11, now(), now(), '유저 계정 메모', '유저 계정10', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user10', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (12, now(), now(), '유저 계정 메모', '유저 계정11', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user11', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (13, now(), now(), '유저 계정 메모', '유저 계정12', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user12', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (14, now(), now(), '유저 계정 메모', '유저 계정13', '$2a$10$03paiGhUQ8EA7gAMTAqviu2dXJ2SpfvQaOIaqU/UFvZNm//DUVrZm', 'user13', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (15, now(), now(), '유저 계정 메모', '유저 계정14', '$2a$10$zrRQ7LECjH9CevfadmRTCe3W8.2eK3hUd.Kah8dBme/qpY0FPaByO', 'user14', 2, 1 ,'CILP', 7);

INSERT INTO user (id, created_at, updated_at, memo, nickname, password, user_id, admin_id, approved_level, type_inspection, user_category_id)
VALUES (16, now(), now(), '유저 계정 메모', '유저 계정15', '$2a$10$zrRQ7LECjH9CevfadmRTCe3W8.2eK3hUd.Kah8dBme/qpY0FPaByO', 'user15', 2, 1 ,'CILP', 7);


INSERT INTO schedule_color (id, name, text_color)
VALUES (1, 'green', '#00AB55');

INSERT INTO schedule_color (id, name, text_color)
VALUES (2, 'blue', '#1890FF');

INSERT INTO schedule_color (id, name, text_color)
VALUES (3, 'lime', '#94D82D');

INSERT INTO schedule_color (id, name, text_color)
VALUES (4, 'yellow', '#FFC107');

INSERT INTO schedule_color (id, name, text_color)
VALUES (5, 'red', '#FF4842');

INSERT INTO schedule_color (id, name, text_color)
VALUES (6, 'purple', '#04297A');

INSERT INTO schedule_color (id, name, text_color)
VALUES (7, 'orange', '#7A0C2E');

INSERT INTO board_color (id, title, color)
VALUES (1, '빨강', '#FFB4B4');

INSERT INTO board_color (id, title, color)
VALUES (2, '보라', '#DBCAFF');

INSERT INTO board_color (id, title, color)
VALUES (3, '노랑', '#FFEFC3');

INSERT INTO board_color (id, title, color)
VALUES (4, '초록', '#B1E8C1');

INSERT INTO board_color (id, title, color)
VALUES (5, '파랑', '#A7E4FF');

INSERT INTO notice_category (id, created_at, updated_at, memo, title) VALUES (1, '2024-04-09 01:47:59', '2024-04-09 01:47:59', '일반 상황에 쓰이는 공지사항 입니다.', '일반');
INSERT INTO notice_category (id, created_at, updated_at, memo, title) VALUES (2, '2024-04-09 01:47:59', '2024-04-09 01:47:59', '긴급 일때 쓰이는 공지사항 입니다.', '긴급');
INSERT INTO notice_category (id, created_at, updated_at, memo, title) VALUES (3, '2024-04-15 23:47:37', '2024-04-15 23:47:37', '메모', '공지등록');

