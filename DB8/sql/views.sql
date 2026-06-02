CREATE VIEW v_product_catalog AS
SELECT
    p.product_id,
    p.category_id,
    bc.category_name,
    p.product_name,
    p.author,
    p.publisher,
    p.unit_price,
    p.stock_quantity
FROM product p
JOIN book_category bc ON p.category_id = bc.category_id;

CREATE VIEW v_customer_purchase_history AS
SELECT
    s.customer_id,
    s.sales_id,
    s.sales_timestamp,
    p.product_id,
    p.product_name,
    bc.category_name,
    sd.quantity,
    sd.unit_price_at_sale,
    sd.subtotal,
    s.total_amount
FROM sales s
JOIN sales_detail sd ON s.sales_id = sd.sales_id
JOIN product p ON sd.product_id = p.product_id
JOIN book_category bc ON p.category_id = bc.category_id;

CREATE VIEW v_inventory_status AS
SELECT
    p.product_id,
    p.product_name,
    bc.category_name,
    p.author,
    p.publisher,
    p.unit_price,
    p.stock_quantity
FROM product p
JOIN book_category bc ON p.category_id = bc.category_id;

CREATE VIEW v_sales_analysis_summary AS
SELECT
    p.product_id,
    p.product_name,
    bc.category_name,
    COALESCE(ts.total_quantity, 0) AS total_quantity,
    COALESCE(ts.total_revenue, 0.00) AS total_revenue,
    p.unit_price AS current_unit_price,
    p.stock_quantity
FROM product p
JOIN book_category bc ON p.category_id = bc.category_id
LEFT JOIN total_sales ts ON p.product_id = ts.product_id;
