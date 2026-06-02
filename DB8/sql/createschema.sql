CREATE TABLE `book_category` (
  `category_id` INT PRIMARY KEY,
  `category_name` VARCHAR(50) UNIQUE NOT NULL,
  `description` VARCHAR(255)
);

CREATE TABLE `product` (
  `product_id` INT PRIMARY KEY,
  `category_id` INT NOT NULL,
  `product_name` VARCHAR(100) NOT NULL,
  `author` VARCHAR(100),
  `publisher` VARCHAR(100),
  `unit_price` DECIMAL(10,2) NOT NULL,
  `stock_quantity` INT NOT NULL
);

CREATE TABLE `customer` (
  `customer_id` INT PRIMARY KEY,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20),
  `birth_date` DATE NOT NULL,
  `join_date` DATE
);

CREATE TABLE `manager_role` (
  `role_id` INT PRIMARY KEY,
  `role_name` VARCHAR(50) UNIQUE NOT NULL,
  `description` VARCHAR(255)
);

CREATE TABLE `manager` (
  `manager_id` INT PRIMARY KEY,
  `role_id` INT NOT NULL,
  `manager_name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL
);

CREATE TABLE `market_basket` (
  `basket_id` INT AUTO_INCREMENT PRIMARY KEY,
  `customer_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `added_at` TIMESTAMP,
  `unit_price_at_added` DECIMAL(10,2) NOT NULL
);

CREATE TABLE `sales` (
  `sales_id` INT AUTO_INCREMENT PRIMARY KEY,
  `customer_id` INT NOT NULL,
  `sales_timestamp` TIMESTAMP NOT NULL,
  `total_amount` DECIMAL(10,2),
  `profile_id` INT NOT NULL,
  `age_at_sale` INT NOT NULL
);

CREATE TABLE `sales_detail` (
  `sales_detail_id` INT AUTO_INCREMENT PRIMARY KEY,
  `sales_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price_at_sale` DECIMAL(10,2) NOT NULL,
  `subtotal` DECIMAL(10,2)
);

CREATE TABLE `total_sales` (
  `total_sales_id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT UNIQUE NOT NULL,
  `total_quantity` INT NOT NULL,
  `total_revenue` DECIMAL(12,2) NOT NULL
);

CREATE TABLE `product_price_history` (
  `price_history_id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `start_date` TIMESTAMP NOT NULL,
  `end_date` TIMESTAMP
);

CREATE TABLE `book_review` (
  `review_id` INT PRIMARY KEY,
  `customer_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `rating` INT NOT NULL,
  `review_text` VARCHAR(500),
  `review_date` DATE
);

CREATE TABLE `customer_profile_history` (
  `profile_id` INT PRIMARY KEY,
  `customer_id` INT NOT NULL,
  `city` VARCHAR(50),
  `membership_level` VARCHAR(30),
  `start_date` TIMESTAMP NOT NULL,
  `end_date` TIMESTAMP
);

ALTER TABLE `product` ADD FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`);

ALTER TABLE `manager` ADD FOREIGN KEY (`role_id`) REFERENCES `manager_role` (`role_id`);

ALTER TABLE `market_basket` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

ALTER TABLE `market_basket` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

ALTER TABLE `sales` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

ALTER TABLE `sales` ADD FOREIGN KEY (`profile_id`) REFERENCES `customer_profile_history` (`profile_id`);

ALTER TABLE `sales_detail` ADD FOREIGN KEY (`sales_id`) REFERENCES `sales` (`sales_id`);

ALTER TABLE `sales_detail` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

ALTER TABLE `total_sales` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

ALTER TABLE `product_price_history` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

ALTER TABLE `book_review` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

ALTER TABLE `book_review` ADD FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

ALTER TABLE `customer_profile_history` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

CREATE INDEX `idx_product_category` ON `product` (`category_id`);

CREATE INDEX `idx_market_basket_customer` ON `market_basket` (`customer_id`);

CREATE INDEX `idx_sales_customer_timestamp` ON `sales` (`customer_id`, `sales_timestamp`);

CREATE INDEX `idx_sales_detail_product` ON `sales_detail` (`product_id`);

CREATE INDEX `idx_book_review_product` ON `book_review` (`product_id`);
