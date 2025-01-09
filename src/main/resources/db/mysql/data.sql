-- 동물 유형
INSERT INTO `types` (`id`, `name`)
VALUES (1, '개'),
       (2, '고양이');

-- 소유자
INSERT INTO `owner` (`id`, `user_id`, `password`, `name`, `address`, `city`, `telephone`)
VALUES (1, 'goorm1', 'pw1234', '김구름', '서울 강남구', '서울', '010-1234-5678'),
       (2, 'goorm2', 'pw1234', '박구름', '부산 해운대구', '부산', '010-1111-2222');

-- 애완동물
INSERT INTO `pets` (`id`, `name`, `birth_date`, `type_id`, `owner_id`)
VALUES (1, '뿡치', '2020-01-15', 1, 1),
       (2, '부각', '2019-05-20', 2, 2),
       (3, '돌배', '2021-03-10', 2, 1);

-- 수의사
INSERT INTO `vets` (`id`, `name`, `average_ratings`, `review_count`, `status`)
VALUES (1, '이의사', 4.5, 10, 'REGISTERED'),
       (2, '강의사', 4.8, 15, 'REGISTERED');

-- 전문 분야
INSERT INTO `specialties` (`id`, `name`)
VALUES (1, '외과'),
       (2, '소아과'),
       (3, '피부과'),
       (4, '안과');

-- 수의사 전문 분야 연결
INSERT INTO `vet_specialties` (`id`, `vet_id`, `specialty_id`)
VALUES (1, 1, 1),
       (2, 1, 3),
       (3, 2, 2),
       (4, 2, 4);

-- 방문 기록
INSERT INTO `visits` (`id`, `visit_date`, `description`, `pet_id`)
VALUES (1, '2023-01-10 10:30:00', '예방접종', 1),
       (2, '2023-02-15 14:00:00', '건강검진', 2),
       (3, '2023-03-20 09:00:00', '감기', 3);

-- 진료 기록
INSERT INTO `history` (`id`, `symptoms`, `content`, `vet_id`, `visit_id`)
VALUES (1, '기침', '감기 진단 및 처방', 1, 1),
       (2, '피부 발진', '피부 치료', 2, 2);

-- 예약
INSERT INTO `appointment` (`id`, `appt_date`, `status`, `symptoms`, `pet_id`, `vet_id`)
VALUES (1, '2025-01-11', 'COMPLETE', '발열 및 기침', 1, 1),
       (2, '2025-5-10', 'CANCEL', '다리 부상', 2, 2);

-- 리뷰
INSERT INTO `review` (`id`, `score`, `content`, `created_at`, `vet_id`, `owner_id`)
VALUES (1, 5, '아주 굳', '2023-12-25 12:00:00', 1, 1),
       (2, 4, '만족', '2023-12-26 15:00:00', 2, 2);
