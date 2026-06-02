-- Basket / Sales test data for customer_id = 9001 and product_id = 9001

DELETE FROM market_basket
WHERE customer_id IN (9001, 9002)
   OR product_id IN (9001, 9002, 9003);

DELETE FROM sales_detail
WHERE sales_id IN (
    SELECT sales_id
    FROM sales
    WHERE customer_id IN (9001, 9002)
);

DELETE FROM sales
WHERE customer_id IN (9001, 9002);

DELETE FROM total_sales
WHERE product_id IN (9001, 9002, 9003);

DELETE FROM customer_profile_history
WHERE customer_id IN (9001, 9002)
   OR profile_id IN (9001, 9002);

DELETE FROM customer
WHERE customer_id IN (9001, 9002);

DELETE FROM product
WHERE product_id IN (9001, 9002, 9003);

DELETE FROM book_category
WHERE category_id IN (9001, 9002);

INSERT INTO book_category
(category_id, category_name, description)
VALUES
    (9001, 'Test Novel', 'Temporary category for basket and sales test'),
    (9002, 'Test IT', 'Temporary IT category for basket and sales test');

INSERT INTO product
(product_id, category_id, product_name, author, publisher, unit_price, stock_quantity)
VALUES
    (9001, 9001, 'The Test Book', 'Test Author A', 'Test Publisher', 15000.00, 10),
    (9002, 9002, 'Java Database Practice', 'Test Author B', 'Test Publisher', 22000.00, 8),
    (9003, 9001, 'Online Bookstore Story', 'Test Author C', 'Test Publisher', 18000.00, 5);

INSERT INTO customer
(customer_id, first_name, last_name, email, phone, birth_date, join_date)
VALUES
    (9001, 'Yuri', 'Kim', 'yuri.test9001@example.com', '010-9001-0001', '2002-03-15', CURDATE()),
    (9002, 'Test', 'Customer', 'customer.test9002@example.com', '010-9002-0002', '2001-07-21', CURDATE());

INSERT INTO customer_profile_history
(profile_id, customer_id, city, membership_level, start_date, end_date)
VALUES
    (9001, 9001, 'Seoul', 'BASIC', CURRENT_TIMESTAMP, NULL),
    (9002, 9002, 'Busan', 'SILVER', CURRENT_TIMESTAMP, NULL);

SELECT * FROM book_category
WHERE category_id IN (9001, 9002);

SELECT * FROM product
WHERE product_id IN (9001, 9002, 9003);

SELECT * FROM customer
WHERE customer_id IN (9001, 9002);

SELECT * FROM customer_profile_history
WHERE customer_id IN (9001, 9002);

SELECT
    basket_id,
    customer_id,
    product_id,
    quantity,
    added_at,
    unit_price_at_added
FROM market_basket
WHERE customer_id = 9001;

SELECT
    sales_id,
    customer_id,
    sales_timestamp,
    total_amount,
    profile_id,
    age_at_sale
FROM sales
WHERE customer_id = 9001
ORDER BY sales_id DESC;

SELECT
    sd.sales_detail_id,
    sd.sales_id,
    sd.product_id,
    sd.quantity,
    sd.unit_price_at_sale,
    sd.subtotal
FROM sales_detail sd
         JOIN sales s ON sd.sales_id = s.sales_id
WHERE s.customer_id = 9001
ORDER BY sd.sales_detail_id DESC;

SELECT
    total_sales_id,
    product_id,
    total_quantity,
    total_revenue
FROM total_sales
WHERE product_id = 9001;

SELECT
    product_id,
    product_name,
    unit_price,
    stock_quantity
FROM product
WHERE product_id = 9001;