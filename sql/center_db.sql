-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema center_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema center_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `center_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `center_db` ;

-- -----------------------------------------------------
-- Table `center_db`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `updated_at` TIMESTAMP NULL DEFAULT NULL,
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  `status` ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING', 'DELETED') NOT NULL,
  `cpf` VARCHAR(45) NOT NULL,
  `birth_date` VARCHAR(8) NOT NULL,
  `email_verified_at` TIMESTAMP NULL,
  `last_login` TIMESTAMP NULL,
  `phone` VARCHAR(13) NULL,
  `last_exit` TIMESTAMP NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `cpf_UNIQUE` (`cpf` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`addresses` (
  `address_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `recipient_name` VARCHAR(150) NOT NULL,
  `street` VARCHAR(150) NOT NULL,
  `number` VARCHAR(10) NOT NULL,
  `complement` VARCHAR(50) NULL DEFAULT NULL,
  `district` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `state` VARCHAR(100) NOT NULL,
  `postal_code` VARCHAR(20) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `is_primary` TINYINT NOT NULL,
  `updated_at` TIMESTAMP NULL,
  `address_type` ENUM('RESIDENTIAL', 'COMMERCIAL', 'OTHER') NULL,
  PRIMARY KEY (`address_id`),
  INDEX `fk_addresses_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_addresses_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `center_db`.`users` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`categories` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100) NOT NULL,
  `parent_category_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE INDEX `category_name_UNIQUE` (`category_name` ASC) VISIBLE,
  INDEX `fk_categories_parent1_idx` (`parent_category_id` ASC) VISIBLE,
  CONSTRAINT `fk_categories_parent1`
    FOREIGN KEY (`parent_category_id`)
    REFERENCES `center_db`.`categories` (`category_id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`orders` (
  `order_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `order_date` DATETIME NOT NULL,
  `status` ENUM('PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') NOT NULL,
  `total` DECIMAL(10,2) NOT NULL,
  `shipping_address_id` INT NOT NULL,
  PRIMARY KEY (`order_id`),
  INDEX `fk_orders_addresses1_idx` (`shipping_address_id` ASC) VISIBLE,
  INDEX `fk_orders_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_orders_addresses1`
    FOREIGN KEY (`shipping_address_id`)
    REFERENCES `center_db`.`addresses` (`address_id`),
  CONSTRAINT `fk_orders_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `center_db`.`users` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`suppliers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`suppliers` (
  `supplier_id` INT NOT NULL AUTO_INCREMENT,
  `supplier_name` VARCHAR(150) NOT NULL,
  `cnpj` VARCHAR(20) NOT NULL,
  `approved` TINYINT NOT NULL,
  `db_url` VARCHAR(250) NULL DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `updated_at` TIMESTAMP NULL DEFAULT NULL,
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  `status` ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING', 'DELETED') NOT NULL,
  `contact_email` VARCHAR(255) NULL,
  `contact_phone` VARCHAR(13) NULL,
  `commission_rate` DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`supplier_id`),
  UNIQUE INDEX `supplier_name_UNIQUE` (`supplier_name` ASC) VISIBLE,
  UNIQUE INDEX `cnpj_UNIQUE` (`cnpj` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`products`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`products` (
  `products_id` INT NOT NULL AUTO_INCREMENT,
  `supplier_id` INT NOT NULL,
  `product_name` VARCHAR(60) NOT NULL,
  `description` TEXT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL,
  `status` ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'DISCONTINUED') NOT NULL,
  PRIMARY KEY (`products_id`),
  INDEX `supplier_id_idx` (`supplier_id` ASC) VISIBLE,
  CONSTRAINT `fk_supplier_id`
    FOREIGN KEY (`supplier_id`)
    REFERENCES `center_db`.`suppliers` (`supplier_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`product_variants`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`product_variants` (
  `variant_id` INT NOT NULL AUTO_INCREMENT,
  `product_id` INT NOT NULL,
  `sku` VARCHAR(100) NOT NULL,
  `attributes` JSON NULL DEFAULT NULL,
  `price_adjustment` DECIMAL(10,2) NULL DEFAULT '0.00',
  `cost_price` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `stock` INT NOT NULL,
  `status` ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'DISCONTINUED') NOT NULL,
  `img_url` TEXT NOT NULL,
  `supplier_product_id` VARCHAR(100) NULL,
  PRIMARY KEY (`variant_id`),
  UNIQUE INDEX `sku_UNIQUE` (`sku` ASC) VISIBLE,
  INDEX `fk_product_variants_products1_idx` (`product_id` ASC) VISIBLE,
  CONSTRAINT `fk_product_variants_products1`
    FOREIGN KEY (`product_id`)
    REFERENCES `center_db`.`products` (`products_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`supplier_shipments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`supplier_shipments` (
  `shipment_id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `supplier_id` INT NOT NULL,
  `status` ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
  `shipping_service` VARCHAR(100) NULL DEFAULT NULL,
  `shipping_cost` DECIMAL(10,2) NULL DEFAULT NULL,
  `tracking_code` VARCHAR(100) NULL DEFAULT NULL,
  `shipped_at` DATETIME NULL DEFAULT NULL,
  `delivered_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`shipment_id`),
  INDEX `fk_supplier_shipments_orders1_idx` (`order_id` ASC) VISIBLE,
  INDEX `fk_supplier_shipments_suppliers1_idx` (`supplier_id` ASC) VISIBLE,
  CONSTRAINT `fk_supplier_shipments_orders1`
    FOREIGN KEY (`order_id`)
    REFERENCES `center_db`.`orders` (`order_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_supplier_shipments_suppliers1`
    FOREIGN KEY (`supplier_id`)
    REFERENCES `center_db`.`suppliers` (`supplier_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`order_items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`order_items` (
  `order_id` INT NOT NULL,
  `shipment_id` INT NULL DEFAULT NULL,
  `variant_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`order_id`, `variant_id`),
  INDEX `fk_order_items_products1_idx` (`variant_id` ASC) VISIBLE,
  INDEX `fk_order_items_supplier_shipments1` (`shipment_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_items_orders1`
    FOREIGN KEY (`order_id`)
    REFERENCES `center_db`.`orders` (`order_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_product_variants1`
    FOREIGN KEY (`variant_id`)
    REFERENCES `center_db`.`product_variants` (`variant_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_supplier_shipments1`
    FOREIGN KEY (`shipment_id`)
    REFERENCES `center_db`.`supplier_shipments` (`shipment_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`pay_card`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`pay_card` (
  `card_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `card_last4` VARCHAR(4) NOT NULL,
  `card_brand` VARCHAR(30) NOT NULL,
  `card_expiry` VARCHAR(7) NOT NULL,
  `cardholder_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`card_id`),
  INDEX `fk_pay_card_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_pay_card_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `center_db`.`users` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`payments` (
  `payment_id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `method` VARCHAR(45) NOT NULL,
  `status` ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL,
  `transaction_code` VARCHAR(100) NULL DEFAULT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `paid_at` DATETIME NULL DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL,
  PRIMARY KEY (`payment_id`),
  INDEX `fk_payments_orders1_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_payments_orders1`
    FOREIGN KEY (`order_id`)
    REFERENCES `center_db`.`orders` (`order_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`product_categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`product_categories` (
  `product_id` INT NOT NULL,
  `category_id` INT NOT NULL,
  PRIMARY KEY (`product_id`, `category_id`),
  INDEX `fk_product_categories_categories1_idx` (`category_id` ASC) VISIBLE,
  CONSTRAINT `fk_product_categories_categories1`
    FOREIGN KEY (`category_id`)
    REFERENCES `center_db`.`categories` (`category_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_product_categories_products1`
    FOREIGN KEY (`product_id`)
    REFERENCES `center_db`.`products` (`products_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`roles` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(45) NOT NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_name_UNIQUE` (`role_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`supplier_transactions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`supplier_transactions` (
  `transaction_id` INT NOT NULL AUTO_INCREMENT,
  `supplier_id` INT NOT NULL,
  `shipment_id` INT NULL DEFAULT NULL,
  `type` ENUM('PAYMENT_TO_SUPPLIER', 'REFUND_FROM_SUPPLIER', 'COMMISSION_EARNED') NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `transaction_date` DATETIME NOT NULL,
  `status` ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL,
  `notes` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  INDEX `fk_supplier_transactions_suppliers1_idx` (`supplier_id` ASC) VISIBLE,
  INDEX `fk_supplier_transactions_shipments1_idx` (`shipment_id` ASC) VISIBLE,
  CONSTRAINT `fk_supplier_transactions_shipments1`
    FOREIGN KEY (`shipment_id`)
    REFERENCES `center_db`.`supplier_shipments` (`shipment_id`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_supplier_transactions_suppliers1`
    FOREIGN KEY (`supplier_id`)
    REFERENCES `center_db`.`suppliers` (`supplier_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`supplier_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`supplier_user` (
  `supplier_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `association_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`, `user_id`),
  INDEX `fk_supplier_user_users1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_supplier_user_suppliers1`
    FOREIGN KEY (`supplier_id`)
    REFERENCES `center_db`.`suppliers` (`supplier_id`),
  CONSTRAINT `fk_supplier_user_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `center_db`.`users` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `center_db`.`users_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `center_db`.`users_roles` (
  `role_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `assigned_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`, `user_id`),
  INDEX `role_id_idx` (`role_id` ASC) VISIBLE,
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `center_db`.`roles` (`role_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `center_db`.`users` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
