CREATE TABLE `pets` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `name` VARCHAR(15) NOT NULL,
                      `birth_date` DATE NULL,
                      `type_id` INT NOT NULL,
                      `owner_id` INT NOT NULL,
                      PRIMARY KEY (`id`)
);

CREATE TABLE `types` (
                       `id` INT NOT NULL AUTO_INCREMENT,
                       `name` VARCHAR(255) NOT NULL,
                       PRIMARY KEY (`id`)
);

CREATE TABLE `visits` (
                        `id` INT NOT NULL AUTO_INCREMENT,
                        `visit_date` DATETIME NOT NULL,
                        `description` VARCHAR(255) NOT NULL,
                        `pet_id` INT NOT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `owner` (
                       `id` INT NOT NULL AUTO_INCREMENT,
                       `user_id` VARCHAR(100) NOT NULL,
                       `password` VARCHAR(100) NOT NULL,
                       `name` VARCHAR(15) NOT NULL,
                       `address` VARCHAR(255) NOT NULL,
                       `city` VARCHAR(15) NOT NULL,
                       `telephone` VARCHAR(15) NOT NULL,
                       PRIMARY KEY (`id`)
);

CREATE TABLE `vets` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `name` VARCHAR(15) NOT NULL,
                      `average_ratings` DECIMAL(3,2) NULL DEFAULT 0,
                      `review_count` INT NULL DEFAULT 0,
                      PRIMARY KEY (`id`)
);

CREATE TABLE `specialties` (
                             `id` INT NOT NULL AUTO_INCREMENT,
                             `name` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`)
);

CREATE TABLE `vet_specialties` (
                                 `id` INT NOT NULL AUTO_INCREMENT,
                                 `vet_id` INT NOT NULL,
                                 `specialty_id` INT NOT NULL,
                                 PRIMARY KEY (`id`)
);

CREATE TABLE `history` (
                         `id` INT NOT NULL AUTO_INCREMENT,
                         `symptoms` VARCHAR(255) NOT NULL,
                         `content` VARCHAR(255) NOT NULL,
                         `vet_id` INT NOT NULL,
                         `visit_id` INT NOT NULL,
                         PRIMARY KEY (`id`)
);

CREATE TABLE `appointment` (
                             `id` INT NOT NULL AUTO_INCREMENT,
                             `appt_date` DATE NOT NULL,
                             `status` enum('COMPLETE', 'CANCEL') NULL,
                             `symptoms` VARCHAR(255) NULL,
                             `pet_id` INT NOT NULL,
                             `vet_id` INT NOT NULL,
                             PRIMARY KEY (`id`)
);

CREATE TABLE `review` (
                        `id` INT NOT NULL AUTO_INCREMENT,
                        `score` INT NOT NULL,
                        `content` VARCHAR(200) NULL,
                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `vet_id` INT NOT NULL,
                        `owner_id` INT NOT NULL,
                        PRIMARY KEY (`id`)
);
